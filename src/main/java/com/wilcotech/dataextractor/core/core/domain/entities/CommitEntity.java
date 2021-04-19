package com.wilcotech.dataextractor.core.core.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "commits")
public class CommitEntity extends AbstractBaseEntity<Long> {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ProjectEntity project;

    @Column(nullable = false,unique = true)
    private String commitId;

    @ManyToOne(fetch = FetchType.EAGER,optional = false)
    private DeveloperEntity developer;

    @Column
    private Instant commitDate;

}
