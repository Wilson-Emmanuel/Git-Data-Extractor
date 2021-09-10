package com.softwarelab.dataextractor.core.persistence.repositories;

import com.softwarelab.dataextractor.core.persistence.entities.CommiterEntity;
import com.softwarelab.dataextractor.core.persistence.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Created by Wilson
 * on Mon, 16/08/2021.
 */
public interface CommiterRepository extends JpaRepository<CommiterEntity,Long> {

    @Query("select c from CommiterEntity c where (c.email = :email or c.name = :name) and c.project = :project")
    Optional<CommiterEntity> getCommiterEntityByNameOrEmail(@Param("email") String email, @Param("name") String name, @Param("project") ProjectEntity project);

    Optional<CommiterEntity> findByEmailAndProject(String email, ProjectEntity project);

    List<CommiterEntity> findAllByProject(ProjectEntity project);
}
