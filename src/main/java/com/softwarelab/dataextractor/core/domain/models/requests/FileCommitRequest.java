package com.softwarelab.dataextractor.core.domain.models.requests;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Wilson
 * on Mon, 19/04/2021.
 */
@Data
@Builder
public class FileCommitRequest {
    private Long fileId;
    private Long commitId;
}
