package com.softwarelab.dataextractor.core.services;

import com.softwarelab.dataextractor.core.persistence.entities.DeveloperEntity;
import com.softwarelab.dataextractor.core.persistence.entities.ProjectEntity;
import com.softwarelab.dataextractor.core.persistence.models.DeveloperModel;
import com.softwarelab.dataextractor.core.persistence.models.PagedData;
import com.softwarelab.dataextractor.core.persistence.models.requests.DeveloperRequest;
import com.softwarelab.dataextractor.core.persistence.repositories.ProjectRepository;
import com.softwarelab.dataextractor.core.services.usecases.CommitAndContentUseCase;
import com.softwarelab.dataextractor.core.persistence.repositories.DeveloperRepository;
import com.softwarelab.dataextractor.core.services.usecases.DeveloperUseCase;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeveloperService implements DeveloperUseCase {

    DeveloperRepository developerRepository;
    ProjectRepository projectRepository;
    CommitAndContentUseCase commitAndContentUseCase;


    @Override
    public DeveloperModel save(DeveloperRequest developerRequest) {
        Optional<ProjectEntity> optionalProjectEntity = projectRepository.findByLocalPath(developerRequest.getProjectPath());
        if(optionalProjectEntity.isEmpty())
            return null;

       DeveloperEntity developerEntity = developerRepository.findByNameAndEmailAndProject_LocalPath(developerRequest.getName(),developerRequest.getEmail(),developerRequest.getProjectPath())
               .orElseGet(() -> developerRepository.save(DeveloperEntity.builder()
                               .name(developerRequest.getName())
                               .email(developerRequest.getEmail())
                               .project(optionalProjectEntity.get())
                               .build()
               ));
        return entityToDeveloperModel(developerEntity);
    }

    @Override
    public boolean existsByNameAndEmailAndProject(String name,String email, String projectPath) {
        return developerRepository.existsByNameAndEmailAndProject_LocalPath(name,email,projectPath);
    }

    @Override
    public DeveloperModel getDeveloperByNameAndEmailAndProject(String name,String email, String projectPath) {
        return developerRepository.findByNameAndEmailAndProject_LocalPath(name,email,projectPath)
                .map(this::entityToDeveloperModel).orElse(null);
    }

    @Override
    public DeveloperModel getDeveloperById(Long id) {
        return developerRepository.findById(id).map(this::entityToDeveloperModel).orElse(null);
    }

    @Override
    public PagedData<DeveloperModel> getAllDeveloperByProject(Long projectId, int page, int size) {
        Optional<ProjectEntity> optionalProjectEntity = projectRepository.findById(projectId);
        if(optionalProjectEntity.isEmpty())
            return new PagedData<>(Collections.emptyList(),0,0);
        Pageable pageable = PageRequest.of(page,size);
        Page<DeveloperEntity> developerEntityPage = developerRepository.findAllByProject(optionalProjectEntity.get(),pageable);
        return new PagedData<>(developerEntityPage.stream().map(this::entityToDeveloperModel).collect(Collectors.toList()),
                developerEntityPage.getTotalElements(),
                developerEntityPage.getTotalPages());
    }

    @Override
    public PagedData<DeveloperModel> getAllDeveloperByProject(String projectPath, int page, int size) {
        Optional<ProjectEntity> optionalProjectEntity = projectRepository.findByLocalPath(projectPath);
        if(optionalProjectEntity.isEmpty())
            return new PagedData<>(Collections.emptyList(),0,0);

        Pageable pageable = PageRequest.of(page,size);
        Page<DeveloperEntity> developerEntityPage = developerRepository.findAllByProject(optionalProjectEntity.get(),pageable);
        return new PagedData<>(developerEntityPage.stream().map(this::entityToDeveloperModel).collect(Collectors.toList()),
                developerEntityPage.getTotalElements(),
                developerEntityPage.getTotalPages());
    }

    @Override
    public PagedData<String> getAllLibrariesUsed(String name,String email, String projectPath, int page, int size) {
        return commitAndContentUseCase.getDevelopersLibraries(name,email,projectPath, page, size);
    }

    @Override
    public PagedData<String> getAllLibrariesUsed(Long id, int page, int size) {
        return commitAndContentUseCase.getDevelopersLibraries(id, page, size);
    }

    private DeveloperModel entityToDeveloperModel(DeveloperEntity developerEntity){
        return DeveloperModel.builder()
                .id(developerEntity.getId())
                .email(developerEntity.getEmail())
                .name(developerEntity.getName())
                .build();
    }
}
