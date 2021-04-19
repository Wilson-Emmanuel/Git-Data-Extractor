package com.wilcotech.dataextractor.core.core.domain.models;

import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
@Data
@Builder
public class FileModel {
    private String nameUrl;
    private DeveloperModel creator;
    private String addedDate;
    @Builder.Default
    private Set<String> libraries = Collections.emptySet();
}
