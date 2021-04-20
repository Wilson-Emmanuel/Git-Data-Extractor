package com.softwarelab.dataextractor.core.domain.models.requests;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Wilson
 * on Mon, 19/04/2021.
 */
@Data
@Builder
public class FileRequest {
    private String projectPath;
    private String nameUrl;
    private String creatorName;
    private String creatorEmail;
    private String addedDate;
}
