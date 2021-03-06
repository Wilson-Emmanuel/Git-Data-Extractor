package com.softwarelab.dataextractor.core.persistence.entities;

import com.softwarelab.dataextractor.core.persistence.entities.AbstractBaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

/**
 * Created by Wilson
 * on Tue, 25/05/2021.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "projects")
public class ProjectEntity extends AbstractBaseEntity<Long> {
    @Column
    private String name;

    @Column(unique = true, nullable = false)
    private String localPath;

    @Column
    private String remoteUrl;
}
