package com.wilcotech.dataextractor.core.core.domain.models.requests;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Wilson
 * on Mon, 19/04/2021.
 */
@Data
@Builder
public class CommitAndContentRequest {
    private Long commitId;
    private Long fileContentId;
}
