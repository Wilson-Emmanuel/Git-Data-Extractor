package com.softwarelab.dataextractor.core.services;

import com.softwarelab.dataextractor.core.persistence.entities.*;
import com.softwarelab.dataextractor.core.persistence.models.FileCountModel;
import com.softwarelab.dataextractor.core.persistence.models.FileModel;
import com.softwarelab.dataextractor.core.persistence.models.PagedData;
import com.softwarelab.dataextractor.core.persistence.models.requests.FileRequest;
import com.softwarelab.dataextractor.core.persistence.repositories.*;
import com.softwarelab.dataextractor.core.services.usecases.FileUseCase;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class FileService implements FileUseCase {
    FileRepository fileRepository;
    ProjectRepository projectRepository;
    DeveloperRepository developerRepository;
    FileContentRepository fileContentRepository;
    FilePackageRepository filePackageRepository;

    @Override
    public FileModel save(FileRequest fileRequest) {
        Optional<ProjectEntity> optionalProjectEntity = projectRepository.findByLocalPath(fileRequest.getProjectPath());
        if(optionalProjectEntity.isEmpty())
            return null;

        FileEntity fileEntity = fileRepository.findByNameUrlAndProject_LocalPath(fileRequest.getNameUrl(),fileRequest.getProjectPath())
                .orElseGet(() ->fileRepository.save(FileEntity.builder()
                                .nameUrl(fileRequest.getNameUrl())
                                .project(optionalProjectEntity.get())
                                .build()
                ));
        Set<FileContentEntity> savedFileContentEntities = new HashSet<>();
        List<FileContentEntity> unsavedFileContentEntityList = new ArrayList<>();
        FileContentEntity fileContentEntity;
        for(String library: fileRequest.getLibraries()){
            //check if it's project library
            if(filePackageRepository.existsFilePackageLike(library.substring(library.lastIndexOf(".")),fileRequest.getProjectPath())){
                continue;
            }
            //check if it's already saved
            fileContentEntity = fileContentRepository.findAllByFileAndLibrary(fileEntity,library).orElse(null);
            if(fileContentEntity != null){
                savedFileContentEntities.add(fileContentEntity);
            }else{
                unsavedFileContentEntityList.add(
                        FileContentEntity.builder()
                        .file(fileEntity)
                        .library(library)
                        .build());
            }
        }
        if(!unsavedFileContentEntityList.isEmpty()){
            savedFileContentEntities.addAll(fileContentRepository.saveAll(unsavedFileContentEntityList));
        }
        return createFileModel(fileEntity,savedFileContentEntities);
    }

    @Override
    public FileCountModel saveBatch(List<FileRequest> fileRequestList) {
        if(fileRequestList.isEmpty())
            return new FileCountModel();

        Optional<ProjectEntity> optionalProjectEntity = projectRepository.findByLocalPath(fileRequestList.get(0).getProjectPath());
        if(optionalProjectEntity.isEmpty())
            return new FileCountModel();

        FileCountModel fileCountModel = new FileCountModel();
        FileEntity fileEntity;
        FileContentEntity fileContentEntity;
        List<FileContentEntity> fileContentEntities = new ArrayList<>();

        for(FileRequest fileRequest: fileRequestList){

             fileEntity = fileRepository.findByNameUrlAndProject_LocalPath(fileRequest.getNameUrl(),fileRequest.getProjectPath())
                    .orElseGet(() ->{
                        fileCountModel.fileCount++;
                        return fileRepository.save(FileEntity.builder()
                                .nameUrl(fileRequest.getNameUrl())
                                .project(optionalProjectEntity.get())
                                .build());
                    });

            for(String library: fileRequest.getLibraries()){
                //check if it's project library
                if(filePackageRepository.existsFilePackageLike(library.substring(0,library.lastIndexOf(".")),fileRequest.getProjectPath())){
                    continue;
                }
                //check if it's already saved
                if(!fileContentRepository.existsByFileAndLibrary(fileEntity,library)) {
                    fileContentEntity = FileContentEntity.builder()
                            .file(fileEntity)
                            .library(library)
                            .build();
                    fileContentEntities.add(fileContentEntity);
                }
            }
        }
        fileCountModel.libraryCount = fileContentRepository.saveAll(fileContentEntities).size();
        return fileCountModel;
    }

    @Override
    public PagedData<FileModel> getProjectFiles(Long projectId, int page, int size) {
        Optional<ProjectEntity> projectEntity = projectRepository.findById(projectId);
        return processProjectFiles(page, size, projectEntity);
    }
    @Override
    public PagedData<FileModel> getProjectFiles(String projectPath, int page, int size) {
        Optional<ProjectEntity> projectEntity = projectRepository.findByLocalPath(projectPath);
        return processProjectFiles(page, size, projectEntity);
    }

    private PagedData<FileModel> processProjectFiles(int page, int size, Optional<ProjectEntity> projectEntity) {
        if(projectEntity.isEmpty())
            return new PagedData<>(Collections.emptyList(),0,0);

        Pageable pageable = PageRequest.of(page, size);
        Page<FileEntity> fileEntityPage = fileRepository.findAllByProject(projectEntity.get(),pageable);

        return new PagedData<>(fileEntityPage.stream().map(this::entityToFile).collect(Collectors.toList()),
                fileEntityPage.getTotalElements(),
                fileEntityPage.getTotalPages());
    }



    @Override
    public boolean existsByNameUrlAndProject(String nameUrl, String projectPath) {
        return fileRepository.existsByNameUrlAndProject_LocalPath(nameUrl,projectPath);
    }

    @Override
    public Set<String> getAllLibrariesByNameUrlAndProject(String nameUrl, String projectPath) {
        Optional<FileEntity> optionalFileEntity = fileRepository.findByNameUrlAndProject_LocalPath(nameUrl,projectPath);
        if(optionalFileEntity.isEmpty())
            return Collections.emptySet();

        return fileContentRepository.findAllByFile(optionalFileEntity.get())
                .stream().map(FileContentEntity::getLibrary).collect(Collectors.toSet());
    }
@Override
    public FileModel entityToFile(FileEntity fileEntity){
       Set<FileContentEntity> fileContentEntities = fileContentRepository.findAllByFile(fileEntity);
       return createFileModel(fileEntity,fileContentEntities);
    }
    private FileModel createFileModel(FileEntity fileEntity, Set<FileContentEntity> fileContentEntities){
        return FileModel.builder()
                .id(fileEntity.getId())
                .nameUrl(fileEntity.getNameUrl())
                .libraries(fileContentEntities
                        .stream().map(FileContentEntity::getLibrary).collect(Collectors.toSet()))
                .build();
    }
}
