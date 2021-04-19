package com.wilcotech.dataextractor.core.core.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "file_contents")
public class FileContentEntity extends AbstractBaseEntity<Long>{
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private FileEntity file;

    @Column(nullable = false)
    private String library;
}
