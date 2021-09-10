package com.softwarelab.dataextractor.core.persistence.models.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Wilson
 * on Mon, 19/04/2021.
 */
@Data
@Builder
public class CommitModel {
    private String commitId;
    private String commiterName;
    private String commiterEmail;
    private String authorName;
    private String authorEmail;
    private String commitDate;
    private String authorDate;
    private String fileUrl;
    @Builder.Default
    private Set<String> libraries = new HashSet<>();
}
