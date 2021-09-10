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
@Table(name = "commit_libraries")
public class CommitLibraryEntity extends AbstractBaseEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
   private CommitEntity commit;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private LibraryEntity library;

    @Column
    @Builder.Default
    private Integer committerLibraryCount = 0;
}
