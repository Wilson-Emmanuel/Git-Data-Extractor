package com.softwarelab.dataextractor.core.persistence.services;

import com.softwarelab.dataextractor.core.persistence.entities.CommitEntity;
import com.softwarelab.dataextractor.core.persistence.entities.FileCommitEntity;
import com.softwarelab.dataextractor.core.persistence.entities.FileEntity;
import com.softwarelab.dataextractor.core.persistence.models.CommitModel;
import com.softwarelab.dataextractor.core.persistence.models.FileModel;
import com.softwarelab.dataextractor.core.persistence.models.PagedData;
import com.softwarelab.dataextractor.core.persistence.repositories.CommitRepository;
import com.softwarelab.dataextractor.core.persistence.repositories.FileCommitRepository;
import com.softwarelab.dataextractor.core.persistence.repositories.FileRepository;
import com.softwarelab.dataextractor.core.persistence.services.usecases.CommitUseCase;
import com.softwarelab.dataextractor.core.persistence.services.usecases.FileCommitUseCase;
import com.softwarelab.dataextractor.core.persistence.services.usecases.FileUseCase;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Wilson
 * on Mon, 19/04/2021.
 */
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileCommitService implements FileCommitUseCase {
    CommitUseCase commitUseCase;
    FileUseCase fileUseCase;
    FileRepository fileRepository;
    CommitRepository commitRepository;
    FileCommitRepository fileCommitRepository;

    @Override
    public boolean existsByFileAndCommit(Long fileId, Long commitId) {
        Optional<CommitEntity> optionalCommitEntity = commitRepository.findById(commitId);
        if(optionalCommitEntity.isEmpty())
            return false;

        Optional<FileEntity> optionalFileEntity = fileRepository.findById(fileId);
        if(optionalFileEntity.isEmpty())
            return false;

        return fileCommitRepository.existsByCommitAndFile(optionalCommitEntity.get(),optionalFileEntity.get());
    }

    @Override
    public void save(Long fileId, String commitId) {
        Optional<CommitEntity> optionalCommitEntity = commitRepository.findByCommitId(commitId);
        Optional<FileEntity> optionalFileEntity = fileRepository.findById(fileId);
        if(optionalCommitEntity.isEmpty() || optionalFileEntity.isEmpty())
            return;

        if(!fileCommitRepository.existsByCommitAndFile(optionalCommitEntity.get(),optionalFileEntity.get()))
            fileCommitRepository.save(FileCommitEntity.builder()
                    .commit(optionalCommitEntity.get())
                    .file(optionalFileEntity.get())
                    .build());
    }

    @Override
    public PagedData<CommitModel> getAllCommitsInFile(Long fileId, int page, int size) {
        Optional<FileEntity> optionalFileEntity = fileRepository.findById(fileId);
        if(optionalFileEntity.isEmpty())
            return new PagedData<>(Collections.emptyList(),0,0);

        Pageable pageable = PageRequest.of(page,size);
        Page<FileCommitEntity> fileCommitEntityPage = fileCommitRepository.findAllByFile(optionalFileEntity.get(),pageable);

        return new PagedData<>(fileCommitEntityPage.stream().map(FileCommitEntity::getCommit).map(commitUseCase::entityToCommit).collect(Collectors.toList()),
        fileCommitEntityPage.getTotalElements(),
                fileCommitEntityPage.getTotalPages());
    }

    @Override
    public List<FileModel> getAllFilesInCommit(Long commitId) {
        Optional<CommitEntity> optionalCommitEntity = commitRepository.findById(commitId);
        if(optionalCommitEntity.isEmpty())
            return Collections.emptyList();

        return fileCommitRepository.findAllByCommit(optionalCommitEntity.get())
                .stream().map(FileCommitEntity::getFile).map(fileUseCase::entityToFile).collect(Collectors.toList());
    }


}
