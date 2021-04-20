package com.softwarelab.dataextractor.core.domain.entities;

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
@Table(name = "commits")
public class CommitEntity extends AbstractBaseEntity<Long> {

    @Column(nullable = false,unique = true)
    private String commitId;

    @ManyToOne(fetch = FetchType.EAGER,optional = false)
    private DeveloperEntity developer;

    @Column
    private Instant commitDate;

}
