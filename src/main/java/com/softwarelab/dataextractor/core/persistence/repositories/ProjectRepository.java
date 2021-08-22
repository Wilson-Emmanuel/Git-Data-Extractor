package com.softwarelab.dataextractor.core.persistence.repositories;

import com.softwarelab.dataextractor.core.persistence.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by Wilson
 * on Tue, 25/05/2021.
 */
public interface ProjectRepository extends JpaRepository<ProjectEntity,Long> {
    boolean existsByRemoteUrl(String remoteUrl);
    boolean existsByLocalPath(String localPath);

    Optional<ProjectEntity> findByRemoteUrl(String remoteUrl);
}
