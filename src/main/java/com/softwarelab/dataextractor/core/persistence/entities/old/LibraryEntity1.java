package com.softwarelab.dataextractor.core.persistence.entities.old;

import com.softwarelab.dataextractor.core.persistence.entities.AbstractBaseEntity;
import com.softwarelab.dataextractor.core.persistence.entities.old.CommitEntity;
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
@Table(name = "libraries1")
public class LibraryEntity1 extends AbstractBaseEntity<Long> {

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private CommitEntity commit;

    @Column(nullable = false)
    private String library;
}
