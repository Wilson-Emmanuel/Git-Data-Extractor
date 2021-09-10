package com.softwarelab.dataextractor.core.persistence.repositories;

import com.softwarelab.dataextractor.core.persistence.entities.ClassFileEntity;
import com.softwarelab.dataextractor.core.persistence.entities.CommitEntity;
import com.softwarelab.dataextractor.core.persistence.entities.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
    List<CommitEntity> findAllByClassFile(ClassFileEntity classFile);
    Page<CommitEntity> findAllByClassFile_Project(ProjectEntity project, Pageable pageable);
}
