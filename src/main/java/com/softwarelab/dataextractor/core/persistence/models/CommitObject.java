package com.softwarelab.dataextractor.core.persistence.models;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * Created by Wilson
 * on Thu, 02/09/2021.
 */
@Data
@Builder
public class CommitObject {
    private Long id;
    private String commitId;
    private String authorName;
    private String authorEmail;
    private String authorDate;
    private String commitDate;
    private ClassFileObject classFile;
    private CommiterObject commiter;
    private Set<LibraryObject> libraries;
}
