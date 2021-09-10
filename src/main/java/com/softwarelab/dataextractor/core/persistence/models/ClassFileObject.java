package com.softwarelab.dataextractor.core.persistence.models;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Wilson
 * on Sun, 15/08/2021.
 */
@Data
@Builder
public class ClassFileObject {
    private Long id;
    private String className;
    private String fullPath;
    private ProjectObject project;
}
