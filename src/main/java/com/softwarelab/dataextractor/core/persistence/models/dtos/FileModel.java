package com.softwarelab.dataextractor.core.persistence.models.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.Set;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
@Data
@Builder
public class FileModel {
    private Long id;
    private String nameUrl;
    private String packageName;
    @Builder.Default
    private Set<String> libraries = Collections.emptySet();
}
