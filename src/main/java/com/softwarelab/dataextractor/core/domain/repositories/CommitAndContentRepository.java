package com.softwarelab.dataextractor.core.domain.repositories;

import com.softwarelab.dataextractor.core.domain.entities.*;
import com.wilcotech.dataextractor.core.core.domain.entities.*;
import com.wilcotech.dataextractor.core.domain.entities.*;
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
public interface CommitAndContentRepository extends JpaRepository<CommitAndContentEntity,Long> {
    boolean existsByCommitAndFileContent(CommitEntity commit, FileContentEntity fileContent);
    Set<CommitAndContentEntity> findAllByCommitAndFileContent_File(CommitEntity commit, FileEntity file);
    Page<CommitAndContentEntity> findAllByCommitAndFileContentIsNotNull(CommitEntity commit, Pageable pageable);
    Page<CommitAndContentEntity> findAllByCommit_DeveloperAndFileContentIsNotNull(DeveloperEntity developer, Pageable pageable);
}
