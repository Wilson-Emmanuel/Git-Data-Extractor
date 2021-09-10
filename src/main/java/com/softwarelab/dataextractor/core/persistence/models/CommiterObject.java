package com.softwarelab.dataextractor.core.persistence.models;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Wilson
 * on Sun, 15/08/2021.
 */
@Data
@Builder
public class CommiterObject {
    private Long id;
    private String name;
    private String email;
    private String mappedName;
    private ProjectObject project;
}
