package com.softwarelab.dataextractor.core.persistence.models.dtos;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
@Data
@Builder
public class ProjectModel {
    private Long id;
    private String name;
    private String remoteUrl;
    private String localPath;
}
