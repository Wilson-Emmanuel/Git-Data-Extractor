package com.softwarelab.dataextractor.core.persistence.repositories;

import com.softwarelab.dataextractor.core.persistence.entities.old.CommitEntity;
import com.softwarelab.dataextractor.core.persistence.entities.old.LibraryEntity1;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Wilson
 * on Tue, 25/05/2021.
 */
public interface LibraryRepository1 extends JpaRepository<LibraryEntity1,Long> {
    List<LibraryEntity1> findAllByCommit(CommitEntity commitEntity);
}
