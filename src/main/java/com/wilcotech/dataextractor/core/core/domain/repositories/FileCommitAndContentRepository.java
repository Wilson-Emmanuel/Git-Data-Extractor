package com.wilcotech.dataextractor.core.core.domain.repositories;

import com.wilcotech.dataextractor.core.core.domain.entities.CommitEntity;
import com.wilcotech.dataextractor.core.core.domain.entities.DeveloperEntity;
import com.wilcotech.dataextractor.core.core.domain.entities.FileCommitAndContentEntity;
import com.wilcotech.dataextractor.core.core.domain.entities.FileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
@Repository
public interface FileCommitAndContentRepository extends JpaRepository<FileCommitAndContentEntity,Long> {
    Set<FileCommitAndContentEntity> findAllByFileAndCommitAndFileContentIsNotNull(FileEntity file, CommitEntity commit);
    //Page<FileCommitAndContentEntity> findAllByFileAndFileContentIsNotNull(FileEntity file, Pageable pageable);
    Page<FileCommitAndContentEntity> findAllByCommitAndFileContentIsNotNull(CommitEntity commit, Pageable pageable);
    Page<FileCommitAndContentEntity> findAllByCommit_DeveloperAndFileContentIsNotNull(DeveloperEntity developer,Pageable pageable);
}
