package com.softwarelab.dataextractor.core.persistence.entities;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

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
@Table(name = "commiter_libraries")
public class CommiterLibraryEntity extends AbstractBaseEntity<Long> {
    @Column
    private String authorName;

    @Column
    private String authorEmail;

    @Column
    private Instant commitDate;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private ClassFileEntity classFile;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private CommiterEntity commiter;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private LibraryEntity library;
}