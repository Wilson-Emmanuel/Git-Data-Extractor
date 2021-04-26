package com.softwarelab.dataextractor.core.persistence.entities;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
@Table(name = "file_commit_contents")
public class CommitAndContentEntity extends AbstractBaseEntity<Long>{
    @ManyToOne(fetch = FetchType.EAGER,optional = false)
    private CommitEntity commit;

    @ManyToOne(fetch = FetchType.EAGER,optional = false)
    private FileContentEntity fileContent;
}
