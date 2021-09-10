package com.softwarelab.dataextractor.core.persistence.entities;

import lombok.*;

import javax.persistence.*;

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
@Table(name = "class_files")
public class ClassFileEntity extends AbstractBaseEntity<Long>{

    @Column(nullable = false)
    private String className;

    @Column(nullable = false, unique = true)
    private String fullPath;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ProjectEntity project;
}
