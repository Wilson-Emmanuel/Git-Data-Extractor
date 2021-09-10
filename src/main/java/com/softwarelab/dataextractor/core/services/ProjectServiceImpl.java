package com.softwarelab.dataextractor.core.services;

import com.softwarelab.dataextractor.core.persistence.entities.ClassFileEntity;
import com.softwarelab.dataextractor.core.persistence.entities.ClassLibraryEntity;
import com.softwarelab.dataextractor.core.persistence.entities.LibraryEntity;
import com.softwarelab.dataextractor.core.persistence.entities.ProjectEntity;
import com.softwarelab.dataextractor.core.persistence.models.ProjectObject;
import com.softwarelab.dataextractor.core.persistence.models.dtos.CommitModel;
import com.softwarelab.dataextractor.core.persistence.models.dtos.FileModel;
import com.softwarelab.dataextractor.core.persistence.repositories.ClassFileRepository;
import com.softwarelab.dataextractor.core.persistence.repositories.ClassLibraryRepository;
import com.softwarelab.dataextractor.core.persistence.repositories.LibraryRepository;
import com.softwarelab.dataextractor.core.persistence.repositories.ProjectRepository;
import com.softwarelab.dataextractor.core.services.usecases.ProjectService;
import com.softwarelab.dataextractor.core.services.processors.CMDProcessor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Wilson
 * on Wed, 26/05/2021.
 */
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProjectServiceImpl implements ProjectService {

    ProjectRepository projectRepository;
    CMDProcessor cmdProcessor;
    ClassFileRepository classFileRepository;
    LibraryRepository libraryRepository;
    ClassLibraryRepository classLibraryRepository;

    @Override
    public ProjectObject saveProject(ProjectObject model) {
        Optional<ProjectEntity> optional = projectRepository.findByLocalPath(model.getLocalPath());
        if(optional.isPresent())
            return convertEntityToModel(optional.get());

        ProjectEntity projectEntity = ProjectEntity.builder()
                .localPath(model.getLocalPath())
                .name(model.getName())
                .remoteUrl(model.getRemoteURL())
                .build();
        projectEntity = projectRepository.save(projectEntity);
        return this.convertEntityToModel(projectEntity);
    }

    @Override
    public boolean existsByRemoteUrl(String remoteUrl) {
        return projectRepository.existsByRemoteUrl(remoteUrl);
    }


    @Override
    public boolean existsByLocalPath(String localPath) {
        return projectRepository.existsByLocalPath(localPath);
    }

    @Override
    public boolean isLocalPathValid(String localPath) {
        return cmdProcessor.isValidDir(localPath);
    }

    @Override
    public boolean isValidRemoteURL(String remoteURL) {
        return !remoteURL.isBlank() && remoteURL.endsWith(".git");
    }

    @Override
    public Optional<ProjectObject> getByLocalPath(String urlPath) {
        ProjectEntity projectEntity = projectRepository.findByLocalPath(urlPath).orElse(null);
        return projectEntity == null? Optional.empty(): Optional.of(convertEntityToModel(projectEntity));
    }

    @Override
    public List<ProjectObject> getAllProjects() {
        return projectRepository.findAll().stream().map(this::convertEntityToModel)
                .collect(Collectors.toList());
    }

    /**
     * Save all imports in a Java class file
     * @param packages
     * @param fileModel
     * @param projectObject
     */
    @Override
    public Set<String> saveFiles(Map<String, Boolean> packages, FileModel fileModel, ProjectObject projectObject) {
       Set<String> savedLibraries = new HashSet<>();

        ProjectEntity projectEntity = projectRepository.findById(projectObject.getId()).orElseThrow(()->new RuntimeException("Project not found"));

        //Save the class file

        ClassFileEntity classFileEntity = classFileRepository.findByFullPathAndProject(fileModel.getNameUrl(),projectEntity).orElse(null);
        if(classFileEntity == null){
            classFileEntity = ClassFileEntity.builder()
                    .className(fileModel.getClassName())
                    .fullPath(fileModel.getNameUrl())
                    .project(projectEntity)
                    .build();
            classFileEntity = classFileRepository.save(classFileEntity);
        }

        //Save Imports extracted from the class file
        for (String importResource : fileModel.getLibraries()){

            //Check if the import statement is a package from the current project
            if(!importIsValidAndNotProjectPackage(importResource, packages)){
                continue;
            }

            //Save library if not existing
            LibraryEntity libraryEntity = libraryRepository.findByName(importResource).orElse(null);
            if(libraryEntity == null){
                libraryEntity = LibraryEntity.builder()
                        .name(importResource)
                        .build();
                libraryEntity = libraryRepository.save(libraryEntity);
            }
            savedLibraries.add(libraryEntity.getName());

            //Link the import resource to the project class file
            if(!classLibraryRepository.existsByClassFileAndLibrary(classFileEntity,libraryEntity)){
                ClassLibraryEntity classLibraryEntity = ClassLibraryEntity.builder()
                        .classFile(classFileEntity)
                        .library(libraryEntity)
                        .build();
                classLibraryRepository.save(classLibraryEntity);
            }

        }
        return savedLibraries;
    }

    @Override
    public List<String> getLibraries(String fileFullPath, Long projectId) {
        ProjectEntity projectEntity = projectRepository.findById(projectId).orElseThrow(()->new RuntimeException("Project not found"));


        return null;
    }

    @Override
    public ProjectObject getProject(Long projectId) {
        ProjectEntity projectEntity = projectRepository.findById(projectId).orElseThrow(()->new RuntimeException("Project not found"));
        return this.convertEntityToModel(projectEntity);
    }

    /**
     * A method to ensure the import is valid and not a package in the project
     * @param importResource
     * @param packages
     * @return
     */
    private boolean importIsValidAndNotProjectPackage(String importResource, Map<String, Boolean> packages) {
        if(importResource.isBlank())return false;

        int lastPeriod = importResource.lastIndexOf(".");
        if(lastPeriod >= 0)
            importResource = importResource.substring(0,lastPeriod);//strip off the Class name or asterisk

        return !importResource.isBlank() && !packages.containsKey(importResource);
    }


    private ProjectObject convertEntityToModel(ProjectEntity projectEntity){
        return ProjectObject.builder()
                .id(projectEntity.getId())
                .name(projectEntity.getName())
                .remoteURL(projectEntity.getRemoteUrl())
                .localPath(projectEntity.getLocalPath())
                .build();
    }
}
