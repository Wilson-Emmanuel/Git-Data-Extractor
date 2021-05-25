package com.softwarelab.dataextractor.core.persistence.entities;

import lombok.*;
import org.checkerframework.checker.units.qual.C;

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
@Table(name = "commits")
public class CommitEntity extends AbstractBaseEntity<Long>{
    @Column
    private String commitId;

    @Column
    private String developerName;

    @Column
    private String developerEmail;

    @Column
    private Instant commitDate;

    @Column
    private String fileUrl;
}
