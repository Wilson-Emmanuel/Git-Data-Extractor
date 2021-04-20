package com.softwarelab.dataextractor.core.domain.services;

import com.softwarelab.dataextractor.core.domain.entities.*;
import com.softwarelab.dataextractor.core.domain.models.PagedData;
import com.softwarelab.dataextractor.core.domain.models.requests.CommitAndContentRequest;
import com.softwarelab.dataextractor.core.domain.repositories.*;
import com.wilcotech.dataextractor.core.core.domain.entities.*;
import com.wilcotech.dataextractor.core.core.domain.repositories.*;
import com.wilcotech.dataextractor.core.domain.repositories.*;
import com.softwarelab.dataextractor.core.domain.services.usecases.CommitAndContentUseCase;
import com.wilcotech.dataextractor.core.domain.entities.*;
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
public class CommitAndContentService implements CommitAndContentUseCase {
    CommitAndContentRepository commitAndContentRepository;
    FileRepository fileRepository;
    CommitRepository commitRepository;
    DeveloperRepository developerRepository;
    FileContentRepository fileContentRepository;

    @Override
    public String save(CommitAndContentRequest commitAndContentRequest) {
        Optional<CommitEntity> optionalCommitEntity = commitRepository.findById(commitAndContentRequest.getCommitId());
        if(optionalCommitEntity.isEmpty())
            return null;

        Optional<FileContentEntity> optionalFileContentEntity = fileContentRepository.findById(commitAndContentRequest.getFileContentId());
        if(optionalFileContentEntity.isEmpty())
            return null;

        if(!commitAndContentRepository.existsByCommitAndFileContent(optionalCommitEntity.get(),optionalFileContentEntity.get()))
            commitAndContentRepository.save(CommitAndContentEntity.builder()
                    .commit(optionalCommitEntity.get())
                    .fileContent(optionalFileContentEntity.get())
                    .build());
        return optionalFileContentEntity.get().getLibrary();
    }



    @Override
    public PagedData<String> getDevelopersLibraries(String name, String email, String projectPath, int page, int size) {
        Optional<DeveloperEntity> optionalDeveloperEntity = developerRepository.findByNameAndEmailAndProject_LocalPath(name,email,projectPath);
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
        Page<CommitAndContentEntity> pageItems = commitAndContentRepository.findAllByCommit_DeveloperAndFileContentIsNotNull(optionalDeveloperEntity.get(),pageable);
        return new PagedData<>(pageItems.stream().map(CommitAndContentEntity::getFileContent).map(FileContentEntity::getLibrary).collect(Collectors.toList()),
                pageItems.getTotalElements(),
                pageItems.getTotalPages());
    }

    @Override
    public PagedData<String> getCommitLibraries(String commitId, int page, int size) {
        Optional<CommitEntity> optionalCommitEntity = commitRepository.findByCommitId(commitId);
        if(optionalCommitEntity.isEmpty())
            return new PagedData<>(Collections.emptyList(),0,0);

        Pageable pageable = PageRequest.of(page,size);
        Page<CommitAndContentEntity> pageItems = commitAndContentRepository.findAllByCommitAndFileContentIsNotNull(optionalCommitEntity.get(),pageable);
        return new PagedData<>(pageItems.stream().map(fileContent->fileContent.getFileContent().getLibrary()).collect(Collectors.toList()),
                pageItems.getTotalElements(),
                pageItems.getTotalPages());
    }

    @Override
    public Set<String> getCommitFileLibraries(String commitId, Long fileContentId) {
        Optional<CommitEntity> optionalCommitEntity = commitRepository.findByCommitId(commitId);
        Optional<FileEntity> optionalFileEntity = fileRepository.findById(fileContentId);
        if(optionalCommitEntity.isEmpty() || optionalFileEntity.isEmpty())
            return Collections.emptySet();

        return commitAndContentRepository.findAllByCommitAndFileContent_File(optionalCommitEntity.get(),optionalFileEntity.get())
                .stream().map(a->a.getFileContent().getLibrary()).collect(Collectors.toSet());
    }

    @Override
    public boolean existsByCommitAndFileContentId(String commitId, Long fileContentId) {
        Optional<CommitEntity> optionalCommitEntity = commitRepository.findByCommitId(commitId);
        if(optionalCommitEntity.isEmpty())
            return false;

        Optional<FileContentEntity> optionalFileContentEntity = fileContentRepository.findById(fileContentId);
        if(optionalFileContentEntity.isEmpty())
            return false;

        return commitAndContentRepository.existsByCommitAndFileContent(optionalCommitEntity.get(),optionalFileContentEntity.get());

    }
}
