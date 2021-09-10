package com.softwarelab.dataextractor.core.persistence.entities;

import com.softwarelab.dataextractor.core.persistence.entities.AbstractBaseEntity;
import com.softwarelab.dataextractor.core.persistence.entities.ProjectEntity;
import lombok.*;

import javax.persistence.*;
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
@Table(name = "commits")
public class CommitEntity extends AbstractBaseEntity<Long> {
    @Column(unique = true,nullable = false)
    private String commitId;

    @Column
    private String authorName;

    @Column
    private String authorEmail;

    @Column
    private Instant commitDate;

    @Column
    private Instant authorDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ClassFileEntity classFile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private CommiterEntity commiter;

}
