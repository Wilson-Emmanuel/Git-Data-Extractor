package com.wilcotech.dataextractor.core.core.domain.services;

import com.wilcotech.dataextractor.core.core.domain.entities.DeveloperEntity;
import com.wilcotech.dataextractor.core.core.domain.entities.ProjectEntity;
import com.wilcotech.dataextractor.core.core.domain.models.DeveloperModel;
import com.wilcotech.dataextractor.core.core.domain.models.PagedData;
import com.wilcotech.dataextractor.core.core.domain.repositories.DeveloperRepository;
import com.wilcotech.dataextractor.core.core.domain.repositories.ProjectRepository;
import com.wilcotech.dataextractor.core.core.domain.services.usecases.DeveloperUseCase;
import com.wilcotech.dataextractor.core.core.domain.services.usecases.FileCommitAndContentUseCase;
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
    FileCommitAndContentUseCase fileCommitAndContentUseCase;


    @Override
    public boolean existsByName(String name) {
        return developerRepository.existsByName(name);
    }

    @Override
    public DeveloperModel getDeveloperByName(String name) {
        return developerRepository.findByName(name).map(this::entityToDeveloperModel).orElse(null);
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
    public PagedData<String> getAllLibrariesUsed(String name, int page, int size) {
        return fileCommitAndContentUseCase.getDevelopersLibraries(name, page, size);
    }

    @Override
    public PagedData<String> getAllLibrariesUsed(Long id, int page, int size) {
        return fileCommitAndContentUseCase.getDevelopersLibraries(id, page, size);
    }

    private DeveloperModel entityToDeveloperModel(DeveloperEntity developerEntity){
        return DeveloperModel.builder()
                .id(developerEntity.getId())
                .email(developerEntity.getEmail())
                .name(developerEntity.getName())
                .build();
    }
}
