package com.wilcotech.dataextractor.core.core.domain.models.requests;

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
