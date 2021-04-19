package com.wilcotech.dataextractor.core.core.domain.repositories;

import com.wilcotech.dataextractor.core.core.domain.entities.CommitEntity;
import com.wilcotech.dataextractor.core.core.domain.entities.DeveloperEntity;
import com.wilcotech.dataextractor.core.core.domain.entities.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
@Repository
public interface CommitRepository extends JpaRepository<CommitEntity,Long> {

    Optional<CommitEntity> findByCommitId(String commitId);
    boolean existsByCommitId(String commitId);

    List<CommitEntity> findAllByProject(ProjectEntity project);
    Page<CommitEntity> findAllByProject(ProjectEntity project, Pageable pageable);

    List<CommitEntity> findAllByDeveloper(DeveloperEntity developer);
    Page<CommitEntity> findAllByDeveloper(DeveloperEntity developer,Pageable pageable);

    List<CommitEntity> findAllByCommitDateIsGreaterThanEqual(Instant commitDate);
    Page<CommitEntity> findAllByCommitDateIsGreaterThanEqual(Instant commitDate,Pageable pageable);
}
