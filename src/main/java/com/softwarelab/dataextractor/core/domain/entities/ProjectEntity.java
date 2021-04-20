package com.softwarelab.dataextractor.core.domain.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "projects")
public class ProjectEntity extends AbstractBaseEntity<Long> {
    @Column(nullable = false,unique = true)
    private String name;

    @Column
    private String remoteUrl;

    @Column(nullable = false,unique = true)
    private String localPath;//this is the base url to the folder that contains the .git installed

}
