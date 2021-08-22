package com.softwarelab.dataextractor.core.persistence.repositories;

import com.softwarelab.dataextractor.core.persistence.entities.ClassFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Wilson
 * on Mon, 16/08/2021.
 */
@Repository
public interface ClassFileRepository extends JpaRepository<ClassFileEntity,Long> {
}
