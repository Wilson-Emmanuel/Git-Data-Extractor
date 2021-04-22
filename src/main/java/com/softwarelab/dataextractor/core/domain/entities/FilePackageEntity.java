package com.softwarelab.dataextractor.core.domain.entities;

import lombok.*;

import javax.persistence.*;

/**
 * Created by Wilson
 * on Wed, 21/04/2021.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "file_packages")
public class FilePackageEntity extends AbstractBaseEntity<Long>{
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private ProjectEntity project;

    @Column(nullable = false, unique = true)
    private String packageName;
}
