package com.softwarelab.dataextractor.core.persistence.repositories;

import com.softwarelab.dataextractor.core.persistence.entities.CommitEntity;
import com.softwarelab.dataextractor.core.persistence.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
@Repository
public interface CommitRepository extends JpaRepository<CommitEntity,Long> {

    Optional<CommitEntity> findByCommitId(String commitId);
    boolean existsByCommitId(String commitId);
    boolean existsByCommitIdAndProjectAndDeveloperName(String commitId, ProjectEntity project, String developerName);

}
