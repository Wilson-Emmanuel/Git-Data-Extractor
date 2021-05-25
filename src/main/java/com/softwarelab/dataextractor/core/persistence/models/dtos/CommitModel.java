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
    private String developerName;
    private String developerEmail;
    private String commitDate;
    private String fileUrl;
    @Builder.Default
    private Set<String> libraries = new HashSet<>();
}
