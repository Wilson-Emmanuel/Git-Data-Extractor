package com.softwarelab.dataextractor.core.persistence.repositories;

import com.softwarelab.dataextractor.core.persistence.entities.CommiterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Wilson
 * on Mon, 16/08/2021.
 */
public interface CommiterRepository extends JpaRepository<CommiterEntity,Long> {
}
