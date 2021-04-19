package com.wilcotech.dataextractor.core.core.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table
public class FileCommitAndContentEntity extends AbstractBaseEntity<Long>{
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    private FileEntity file;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    private CommitEntity commit;

    @ManyToOne(fetch = FetchType.EAGER,optional = false)
    private FileContentEntity fileContent;
}
