package com.wilcotech.dataextractor.core.core.domain.models;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
@Data
@Builder
public class FileContentModel {
    private String fileName;
    private String library;
    private DeveloperModel creator;
}
