package com.wilcotech.dataextractor.core.core.domain.entities;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ProjectEntity project;

    @Column(nullable = false)
    private String nameUrl;//from the folder where .git is installed

    @ManyToOne(fetch = FetchType.EAGER)
    private DeveloperEntity creator;

    @Column
    private Instant dateAdded;
}
