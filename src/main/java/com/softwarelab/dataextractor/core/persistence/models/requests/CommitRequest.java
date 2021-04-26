package com.softwarelab.dataextractor.core.persistence.models.requests;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Wilson
 * on Mon, 19/04/2021.
 */
@Data
@Builder
public class CommitRequest {
    private String projectPath;
    private String commitId;
    private String developerName;
    private String developerEmail;
    private String commitDate;
}
