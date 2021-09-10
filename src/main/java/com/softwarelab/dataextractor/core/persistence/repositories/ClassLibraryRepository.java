package com.softwarelab.dataextractor.core.persistence.repositories;

import com.softwarelab.dataextractor.core.persistence.entities.ClassFileEntity;
import com.softwarelab.dataextractor.core.persistence.entities.ClassLibraryEntity;
import com.softwarelab.dataextractor.core.persistence.entities.LibraryEntity;
import com.softwarelab.dataextractor.core.persistence.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Wilson
 * on Mon, 16/08/2021.
 */
@Repository
public interface ClassLibraryRepository extends JpaRepository<ClassLibraryEntity,Long> {

    boolean existsByClassFileAndLibrary(ClassFileEntity classLibrary, LibraryEntity library);

    @Query("select distinct c.library from ClassLibraryEntity c where c.classFile.project = :project")
    List<LibraryEntity> getUniqueProjectLibraries(@Param("project") ProjectEntity project);

    @Query("select distinct c.library from ClassLibraryEntity c where c.classFile.project = :project and (c.library.provider is null  or c.library.category is null) ")
    List<LibraryEntity> getUnclassifiedUniqueProjectLibraries(@Param("project") ProjectEntity project);
}
