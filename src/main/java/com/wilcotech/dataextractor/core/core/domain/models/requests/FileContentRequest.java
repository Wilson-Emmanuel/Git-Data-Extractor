package com.wilcotech.dataextractor.core.core.domain.models.requests;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Wilson
 * on Mon, 19/04/2021.
 */
@Data
@Builder
public class FileContentRequest {
    private Long fileId;
    private String library;
}
