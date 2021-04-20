package com.softwarelab.dataextractor.core.domain.repositories;

import com.softwarelab.dataextractor.core.domain.entities.DeveloperEntity;
import com.softwarelab.dataextractor.core.domain.entities.FileEntity;
import com.softwarelab.dataextractor.core.domain.entities.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
@Repository
public interface FileRepository extends JpaRepository<FileEntity,Long> {
    Optional<FileEntity> findByNameUrlAndProject_LocalPath(String nameUrl,String localPath);

    Page<FileEntity> findAllByProject(ProjectEntity project, Pageable pageable);
    List<FileEntity> findAllByCreator(DeveloperEntity creator);
    boolean existsByNameUrlAndProject_LocalPath(String nameUrl, String localPath);
}
