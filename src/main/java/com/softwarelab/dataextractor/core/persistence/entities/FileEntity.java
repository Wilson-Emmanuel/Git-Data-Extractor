package com.softwarelab.dataextractor.core.persistence.entities;

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
@Table(name = "files")
public class FileEntity extends AbstractBaseEntity<Long>{

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private ProjectEntity project;

    @Column(nullable = false)
    private String nameUrl;//from the folder where .git is installed
}
