package com.wilcotech.dataextractor.core.core.domain.services;

import com.wilcotech.dataextractor.core.core.domain.entities.FileContentEntity;
import com.wilcotech.dataextractor.core.core.domain.entities.ProjectEntity;
import com.wilcotech.dataextractor.core.core.domain.models.PagedData;
import com.wilcotech.dataextractor.core.core.domain.repositories.FileContentRepository;
import com.wilcotech.dataextractor.core.core.domain.repositories.ProjectRepository;
import com.wilcotech.dataextractor.core.core.domain.services.usecases.FileContentUseCase;
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
public class FileContentService implements FileContentUseCase {

    FileContentRepository fileContentRepository;
    ProjectRepository projectRepository;

    @Override
    public PagedData<String> getAllLibraries(Long projectId, int page, int size) {
        Optional<ProjectEntity> optionalProjectEntity = projectRepository.findById(projectId);
        if(optionalProjectEntity.isEmpty())
            return new PagedData<>(Collections.emptyList(),0,0);
        Pageable pageable = PageRequest.of(page,size);
        Page<FileContentEntity> fileContentEntityPage = fileContentRepository.findAllByFile_Project(optionalProjectEntity.get(),pageable);
        return new PagedData<>(fileContentEntityPage.stream().map(FileContentEntity::getLibrary).collect(Collectors.toList()),
                fileContentEntityPage.getTotalElements(),
                fileContentEntityPage.getTotalPages());
    }
}
