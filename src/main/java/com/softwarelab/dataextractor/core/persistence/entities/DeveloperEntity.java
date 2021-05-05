package com.softwarelab.dataextractor.core.persistence.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "developers")
public class DeveloperEntity extends AbstractBaseEntity<Long>{
    @Column(nullable = false)
    private String name;

    @Column
    private String email;

    @ManyToOne
    private ProjectEntity project;
}
