package com.softwarelab.dataextractor.core.domain.services;

import com.softwarelab.dataextractor.core.domain.entities.DeveloperEntity;
import com.softwarelab.dataextractor.core.domain.models.DeveloperModel;
import com.softwarelab.dataextractor.core.domain.models.FileModel;
import com.softwarelab.dataextractor.core.domain.entities.FileContentEntity;
import com.softwarelab.dataextractor.core.domain.entities.FileEntity;
import com.softwarelab.dataextractor.core.domain.entities.ProjectEntity;
import com.softwarelab.dataextractor.core.domain.models.PagedData;
import com.softwarelab.dataextractor.core.domain.models.requests.FileRequest;
import com.softwarelab.dataextractor.core.domain.repositories.DeveloperRepository;
import com.softwarelab.dataextractor.core.domain.repositories.FileContentRepository;
import com.softwarelab.dataextractor.core.domain.repositories.FileRepository;
import com.softwarelab.dataextractor.core.domain.repositories.ProjectRepository;
import com.softwarelab.dataextractor.core.domain.services.usecases.FileUseCase;
import com.softwarelab.dataextractor.core.utilities.DateTimeUtil;
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

    @Override
    public FileModel save(FileRequest fileRequest) {
        Optional<ProjectEntity> optionalProjectEntity = projectRepository.findByLocalPath(fileRequest.getProjectPath());
        if(optionalProjectEntity.isEmpty())
            return null;

        Optional<DeveloperEntity> optionalDeveloperEntity = developerRepository.findByNameAndEmailAndProject_LocalPath(fileRequest.getCreatorName(),fileRequest.getCreatorEmail(),fileRequest.getProjectPath());
        if(optionalDeveloperEntity.isEmpty())
            return null;

        FileEntity fileEntity =fileRepository.findByNameUrlAndProject_LocalPath(fileRequest.getNameUrl(),fileRequest.getProjectPath())
                .orElseGet(() ->fileRepository.save(FileEntity.builder()
                                .dateAdded(DateTimeUtil.getInstantTime(fileRequest.getAddedDate()))
                                .nameUrl(fileRequest.getNameUrl())
                                .project(optionalProjectEntity.get())
                                .creator(optionalDeveloperEntity.get())
                                .build()
                ));
        return entityToFile(fileEntity);
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
    public List<FileModel> getAllDeveloperFiles(String name, String email, String projectPath) {
        Optional<DeveloperEntity> optionalDeveloperEntity = developerRepository.findByNameAndEmailAndProject_LocalPath(name,email,projectPath);
        if(optionalDeveloperEntity.isEmpty())
            return Collections.emptyList();

        return fileRepository.findAllByCreator(optionalDeveloperEntity.get())
                .stream().map(this::entityToFile).collect(Collectors.toList());
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
        DeveloperModel developerModel = DeveloperModel.builder()
                .email(fileEntity.getCreator().getEmail())
                .name(fileEntity.getCreator().getName())
                .build();
        return FileModel.builder()
                .id(fileEntity.getId())
                .nameUrl(fileEntity.getNameUrl())
                .creator(developerModel)
                .addedDate(DateTimeUtil.getDateTime(fileEntity.getDateAdded()))
                .build();
    }
}
