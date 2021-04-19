package com.wilcotech.dataextractor.core.core.domain.services;

import com.wilcotech.dataextractor.core.core.domain.entities.CommitEntity;
import com.wilcotech.dataextractor.core.core.domain.entities.DeveloperEntity;
import com.wilcotech.dataextractor.core.core.domain.entities.FileCommitAndContentEntity;
import com.wilcotech.dataextractor.core.core.domain.entities.FileEntity;
import com.wilcotech.dataextractor.core.core.domain.models.PagedData;
import com.wilcotech.dataextractor.core.core.domain.repositories.CommitRepository;
import com.wilcotech.dataextractor.core.core.domain.repositories.DeveloperRepository;
import com.wilcotech.dataextractor.core.core.domain.repositories.FileCommitAndContentRepository;
import com.wilcotech.dataextractor.core.core.domain.repositories.FileRepository;
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
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Wilson
 * on Mon, 19/04/2021.
 */
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileCommitAndContentService implements FileCommitAndContentUseCase {
    FileCommitAndContentRepository fileCommitAndContentRepository;
    FileRepository fileRepository;
    CommitRepository commitRepository;
    DeveloperRepository developerRepository;

    @Override
    public PagedData<String> getDevelopersLibraries(String name, int page, int size) {
        Optional<DeveloperEntity> optionalDeveloperEntity = developerRepository.findByName(name);
        return processDeveloperLibraries(optionalDeveloperEntity,page,size);
    }

    @Override
    public PagedData<String> getDevelopersLibraries(Long developerId, int page, int size) {
        Optional<DeveloperEntity> optionalDeveloperEntity = developerRepository.findById(developerId);
        return processDeveloperLibraries(optionalDeveloperEntity,page,size);
    }
    private PagedData<String> processDeveloperLibraries(Optional<DeveloperEntity> optionalDeveloperEntity, int page, int size){
        if(optionalDeveloperEntity.isEmpty())
            return new PagedData<>(Collections.emptyList(),0,0);
        Pageable pageable = PageRequest.of(page,size);
        Page<FileCommitAndContentEntity> pageItems =fileCommitAndContentRepository.findAllByCommit_DeveloperAndFileContentIsNotNull(optionalDeveloperEntity.get(),pageable);
        return new PagedData<>(pageItems.stream().map(a->a.getFileContent().getLibrary()).collect(Collectors.toList()),
                pageItems.getTotalElements(),
                pageItems.getTotalPages());
    }

    @Override
    public PagedData<String> getCommitLibraries(String commitId, int page, int size) {
        Optional<CommitEntity> optionalCommitEntity = commitRepository.findByCommitId(commitId);
        if(optionalCommitEntity.isEmpty())
            return new PagedData<>(Collections.emptyList(),0,0);
        Pageable pageable = PageRequest.of(page,size);
        Page<FileCommitAndContentEntity> pageItems = fileCommitAndContentRepository.findAllByCommitAndFileContentIsNotNull(optionalCommitEntity.get(),pageable);
        return new PagedData<>(pageItems.stream().map(a->a.getFileContent().getLibrary()).collect(Collectors.toList()),
                pageItems.getTotalElements(),
                pageItems.getTotalPages());
    }

    @Override
    public Set<String> getCommitFileLibraries(String commitId, Long fileId) {
        Optional<CommitEntity> optionalCommitEntity = commitRepository.findByCommitId(commitId);
        Optional<FileEntity> optionalFileEntity = fileRepository.findById(fileId);
        if(optionalCommitEntity.isEmpty() || optionalFileEntity.isEmpty())
            return Collections.emptySet();
        return fileCommitAndContentRepository.findAllByFileAndCommitAndFileContentIsNotNull(optionalFileEntity.get(),optionalCommitEntity.get())
                .stream().map(a->a.getFileContent().getLibrary()).collect(Collectors.toSet());
    }
}
