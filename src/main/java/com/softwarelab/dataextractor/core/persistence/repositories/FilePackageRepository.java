package com.softwarelab.dataextractor.core.persistence.repositories;

import com.softwarelab.dataextractor.core.persistence.entities.FilePackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by Wilson
 * on Wed, 21/04/2021.
 */
@Repository
public interface FilePackageRepository extends JpaRepository<FilePackageEntity,Long> {
    boolean existsByPackageNameAndProject_LocalPath(String packageName, String projectPath);

    @Query("select case when count(p)> 0 then true else false end from FilePackageEntity p where p.project.localPath = :project and p.packageName like concat('%',:base,'%')")
    boolean existsFilePackageLike(@Param("base") String basePackage, @Param("project") String project);
}
