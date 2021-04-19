package com.wilcotech.dataextractor.core.core.domain.repositories;

import com.wilcotech.dataextractor.core.core.domain.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity,Long> {
    Optional<ProjectEntity> findByName(String name);
    Optional<ProjectEntity> findByLocalPath(String localPath);
    boolean existsByNameOrLocalPath(String name, String localPath);
}
