package com.wilcotech.dataextractor.core.core.domain.services;

import com.wilcotech.dataextractor.core.core.domain.entities.DeveloperEntity;
import com.wilcotech.dataextractor.core.core.domain.entities.FileContentEntity;
import com.wilcotech.dataextractor.core.core.domain.entities.FileEntity;
import com.wilcotech.dataextractor.core.core.domain.entities.ProjectEntity;
import com.wilcotech.dataextractor.core.core.domain.models.DeveloperModel;
import com.wilcotech.dataextractor.core.core.domain.models.FileModel;
import com.wilcotech.dataextractor.core.core.domain.models.PagedData;
import com.wilcotech.dataextractor.core.core.domain.repositories.DeveloperRepository;
import com.wilcotech.dataextractor.core.core.domain.repositories.FileContentRepository;
import com.wilcotech.dataextractor.core.core.domain.repositories.FileRepository;
import com.wilcotech.dataextractor.core.core.domain.repositories.ProjectRepository;
import com.wilcotech.dataextractor.core.core.domain.services.usecases.FileUseCase;
import com.wilcotech.dataextractor.core.core.utilities.DateTimeUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
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
    public PagedData<FileModel> getProjectFiles(Long projectId, int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        Optional<ProjectEntity> projectEntity = projectRepository.findById(projectId);
        if(projectEntity.isEmpty())
            return null;
        Page<FileEntity> fileEntityPage = fileRepository.findAllByProject(projectEntity.get(),pageable);

        return new PagedData<>(fileEntityPage.stream().map(this::entityToFile).collect(Collectors.toList()),
                fileEntityPage.getTotalElements(),
                fileEntityPage.getTotalPages());
    }

    @Override
    public List<FileModel> getAllDeveloperFiles(String developerName) {
        Optional<DeveloperEntity> optionalDeveloperEntity = developerRepository.findByName(developerName);
        if(optionalDeveloperEntity.isEmpty())
            return Collections.emptyList();
        return fileRepository.findAllByCreator(optionalDeveloperEntity.get())
                .stream().map(this::entityToFile).collect(Collectors.toList());
    }

    @Override
    public boolean existsByUrlName(String nameUrl) {
        return fileRepository.existsByName_url(nameUrl);
    }

    @Override
    public Set<String> getAllLibraries(String fileName) {
        Optional<FileEntity> optionalFileEntity = fileRepository.findByNameUrl(fileName);
        if(optionalFileEntity.isEmpty())
            return Collections.emptySet();
        return fileContentRepository.findAllByFile(optionalFileEntity.get())
                .stream().map(FileContentEntity::getLibrary).collect(Collectors.toSet());
    }

    private FileModel entityToFile(FileEntity fileEntity){
        DeveloperModel developerModel = DeveloperModel.builder()
                .email(fileEntity.getCreator().getEmail())
                .name(fileEntity.getCreator().getName())
                .build();
        return FileModel.builder()
                .nameUrl(fileEntity.getNameUrl())
                .creator(developerModel)
                .addedDate(DateTimeUtil.getDateTime(fileEntity.getDateAdded()))
                .build();
    }
}
