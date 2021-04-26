package com.softwarelab.dataextractor.core.persistence.entities;

import lombok.*;

import javax.persistence.*;

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
@Table(name = "file_contents")
public class FileContentEntity extends AbstractBaseEntity<Long>{
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private FileEntity file;

    @Column(nullable = false)
    private String library;
}
