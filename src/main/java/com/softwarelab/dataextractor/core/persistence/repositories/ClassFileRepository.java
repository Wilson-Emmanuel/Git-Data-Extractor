package com.softwarelab.dataextractor.core.persistence.repositories;

import com.softwarelab.dataextractor.core.persistence.entities.ClassFileEntity;
import com.softwarelab.dataextractor.core.persistence.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by Wilson
 * on Mon, 16/08/2021.
 */
@Repository
public interface ClassFileRepository extends JpaRepository<ClassFileEntity,Long> {
    boolean existsByFullPath(String fullPath);

    Optional<ClassFileEntity> findByFullPathAndProject(String fullPath, ProjectEntity project);

    List<ClassFileEntity> findAllByFullPathAndProject(String fullPath, ProjectEntity project);
    List<ClassFileEntity> findAllByProject(ProjectEntity project);
}
