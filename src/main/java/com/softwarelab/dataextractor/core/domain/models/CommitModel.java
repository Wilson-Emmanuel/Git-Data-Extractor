package com.softwarelab.dataextractor.core.domain.models;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
@Data
@Builder
public class CommitModel {
    private Long id;
    private String commitId;
    private DeveloperModel developer;
    private String commitDate;
}
