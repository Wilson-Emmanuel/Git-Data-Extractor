package com.softwarelab.dataextractor.core.persistence.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Wilson
 * on Sun, 15/08/2021.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "libraries")
public class LibraryEntity extends AbstractBaseEntity<Long> {
    //this is used for all projects

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String category;

    @Column
    private String provider;
}
