package com.softwarelab.dataextractor.core.persistence.repositories;

import com.softwarelab.dataextractor.core.persistence.entities.LibraryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Wilson
 * on Tue, 25/05/2021.
 */
public interface LibraryRepository extends JpaRepository<LibraryEntity,Long> {
}
