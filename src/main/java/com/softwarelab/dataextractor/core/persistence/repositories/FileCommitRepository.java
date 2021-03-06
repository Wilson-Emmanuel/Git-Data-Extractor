package com.softwarelab.dataextractor.core.persistence.repositories;

import com.softwarelab.dataextractor.core.persistence.entities.CommitEntity;
import com.softwarelab.dataextractor.core.persistence.entities.DeveloperEntity;
import com.softwarelab.dataextractor.core.persistence.entities.FileCommitEntity;
import com.softwarelab.dataextractor.core.persistence.entities.FileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Wilson
 * on Mon, 19/04/2021.
 */
@Repository
public interface FileCommitRepository extends JpaRepository<FileCommitEntity,Long> {
    Page<FileCommitEntity> findAllByFile(FileEntity file, Pageable pageable);
    List<FileCommitEntity> findAllByCommit(CommitEntity entity);
    List<FileCommitEntity> findAllByCommit_Developer(DeveloperEntity developer);
    boolean existsByCommitAndFile(CommitEntity commit, FileEntity file);
}
