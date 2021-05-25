package com.softwarelab.dataextractor.core.persistence.entities;

import lombok.*;

import javax.persistence.*;

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
@Table(name = "libraries")
public class LibraryEntity extends AbstractBaseEntity<Long>{

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private CommitEntity commit;

    @Column(nullable = false)
    private String library;
}
