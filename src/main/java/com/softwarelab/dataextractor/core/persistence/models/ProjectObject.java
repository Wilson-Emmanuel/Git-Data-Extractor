package com.softwarelab.dataextractor.core.persistence.models;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Created by Wilson
 * on Sun, 15/08/2021.
 */
@Data
@Builder
public class ProjectObject {
    private Long id;
    private String name;
    private String localPath;
    private String remoteURL;
}
