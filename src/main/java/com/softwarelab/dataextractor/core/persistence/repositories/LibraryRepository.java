package com.softwarelab.dataextractor.core.persistence.repositories;

import com.softwarelab.dataextractor.core.persistence.entities.LibraryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by Wilson
 * on Mon, 16/08/2021.
 */
@Repository
public interface LibraryRepository extends JpaRepository<LibraryEntity,Long> {
    boolean existsByName(String name);

    Optional<LibraryEntity> findByName(String name);

    @Query("select c from LibraryEntity c where c.category is null or c.provider is null")
    Page<LibraryEntity> getUnclassifiedLibs(Pageable pageable);

}
