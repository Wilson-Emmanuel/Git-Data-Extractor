package com.softwarelab.dataextractor.core.persistence.models;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Created by Wilson
 * on Sun, 15/08/2021.
 */
@Data
@Builder
public class CommiterLibraryObject {
    private Long id;
    private String authorName;
    private String authorEmail;
    private Instant commitDate;
    private ClassFileObject classFile;
    private CommiterObject commiter;
    private LibraryObject library;
}
