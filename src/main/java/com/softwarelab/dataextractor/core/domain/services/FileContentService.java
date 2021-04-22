package com.softwarelab.dataextractor.core.domain.services;

import com.softwarelab.dataextractor.core.domain.entities.FileContentEntity;
import com.softwarelab.dataextractor.core.domain.entities.FileEntity;
import com.softwarelab.dataextractor.core.domain.entities.ProjectEntity;
import com.softwarelab.dataextractor.core.domain.models.PagedData;
import com.softwarelab.dataextractor.core.domain.models.requests.FileContentRequest;
import com.softwarelab.dataextractor.core.domain.repositories.FileContentRepository;
import com.softwarelab.dataextractor.core.domain.repositories.FilePackageRepository;
import com.softwarelab.dataextractor.core.domain.repositories.FileRepository;
import com.softwarelab.dataextractor.core.domain.repositories.ProjectRepository;
import com.softwarelab.dataextractor.core.domain.services.usecases.FileContentUseCase;
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
    FileRepository fileRepository;
    FilePackageRepository filePackageRepository;

    @Override
    public String save(FileContentRequest fileContentRequest) {
        Optional<FileEntity> optionalFileEntity = fileRepository.findById(fileContentRequest.getFileId());
        if(optionalFileEntity.isEmpty())
            return null;

        String library = fileContentRequest.getLibrary();
        if(filePackageRepository.existsFilePackageLike(library.substring(library.lastIndexOf(".")),optionalFileEntity.get().getProject().getLocalPath())){
            return null;
        }
        FileContentEntity fileContentEntity = fileContentRepository.findAllByFileAndLibrary(optionalFileEntity.get(),fileContentRequest.getLibrary())
                .orElseGet(() -> fileContentRepository.save(FileContentEntity.builder()
                        .file(optionalFileEntity.get())
                        .library(fileContentRequest.getLibrary())
                        .build()));
        return fileContentEntity.getLibrary();
    }

    @Override
    public PagedData<String> getProjectAllLibraries(Long projectId, int page, int size) {
        Optional<ProjectEntity> optionalProjectEntity = projectRepository.findById(projectId);
        return getProjectLibraries(page, size, optionalProjectEntity);
    }
    @Override
    public PagedData<String> getProjectAllLibraries(String projectPath, int page, int size) {
        Optional<ProjectEntity> optionalProjectEntity = projectRepository.findByLocalPath(projectPath);
        return getProjectLibraries(page, size, optionalProjectEntity);
    }

    private PagedData<String> getProjectLibraries(int page, int size, Optional<ProjectEntity> optionalProjectEntity) {
        if(optionalProjectEntity.isEmpty())
            return new PagedData<>(Collections.emptyList(),0,0);

        Pageable pageable = PageRequest.of(page, size);
        Page<FileContentEntity> fileContentEntityPage = fileContentRepository.findAllByFile_Project(optionalProjectEntity.get(),pageable);

        return new PagedData<>(fileContentEntityPage.stream().map(FileContentEntity::getLibrary).collect(Collectors.toList()),
                fileContentEntityPage.getTotalElements(),
                fileContentEntityPage.getTotalPages());
    }
}
