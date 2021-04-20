package com.softwarelab.dataextractor.core.domain.repositories;

import com.softwarelab.dataextractor.core.domain.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity,Long> {
    Optional<ProjectEntity> findByLocalPath(String localPath);
    Optional<ProjectEntity> findByRemoteUrl(String remoteUrl);
    boolean existsByLocalPath(String localPath);
    boolean existsByRemoteUrl(String remoteUrl);
}
