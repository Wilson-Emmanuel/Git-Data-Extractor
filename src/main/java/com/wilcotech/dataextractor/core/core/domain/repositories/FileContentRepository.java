package com.wilcotech.dataextractor.core.core.domain.repositories;

import com.wilcotech.dataextractor.core.core.domain.entities.FileContentEntity;
import com.wilcotech.dataextractor.core.core.domain.entities.FileEntity;
import com.wilcotech.dataextractor.core.core.domain.entities.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
@Repository
public interface FileContentRepository extends JpaRepository<FileContentEntity, Long> {

    //Optional<FileContentEntity> findByLibrary(String library);
    Set<FileContentEntity> findAllByFile(FileEntity file);

    Page<FileContentEntity> findAllByFile_Project(ProjectEntity project,Pageable pageable);
}
