package com.softwarelab.dataextractor.core.persistence.repositories;

import com.softwarelab.dataextractor.core.persistence.entities.FileContentEntity;
import com.softwarelab.dataextractor.core.persistence.entities.FileEntity;
import com.softwarelab.dataextractor.core.persistence.entities.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
@Repository
public interface FileContentRepository extends JpaRepository<FileContentEntity, Long> {
    boolean existsByFileAndLibrary(FileEntity fileEntity, String library);
    Set<FileContentEntity> findAllByFile(FileEntity file);
    Optional<FileContentEntity> findByFileAndLibrary(FileEntity file, String library);
    Page<FileContentEntity> findAllByFile_Project(ProjectEntity project, Pageable pageable);
}
