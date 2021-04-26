package com.softwarelab.dataextractor.core.persistence.repositories;

import com.softwarelab.dataextractor.core.persistence.entities.DeveloperEntity;
import com.softwarelab.dataextractor.core.persistence.entities.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
@Repository
public interface DeveloperRepository extends JpaRepository<DeveloperEntity,Long> {
    //Optional<DeveloperEntity> findByNameAndEmailAndProject(String name, String email, ProjectEntity projectEntity);
    Optional<DeveloperEntity> findByNameAndEmailAndProject_LocalPath(String name, String email, String localPath);
    Page<DeveloperEntity> findAllByProject(ProjectEntity project, Pageable pageable);
    boolean existsByNameAndEmailAndProject_LocalPath(String name, String email, String projectPath);
}
