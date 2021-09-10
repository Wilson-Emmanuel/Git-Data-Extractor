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
@Table(name = "commiters")
public class CommiterEntity extends AbstractBaseEntity<Long>{
    @Column
    private String name;

    @Column
    private String email;

    @Column
    private String mappedName;//this is the real contributors name used for analysis

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ProjectEntity project;
}
