package com.wilcotech.dataextractor.core.core.domain.entities;

import lombok.*;

import javax.persistence.*;

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
