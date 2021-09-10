package com.softwarelab.dataextractor.core.persistence.entities;

import lombok.*;

import javax.persistence.*;

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
@Table(name = "class_libraries")
public class ClassLibraryEntity extends AbstractBaseEntity<Long>{
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private LibraryEntity library;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ClassFileEntity classFile;
}
