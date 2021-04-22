package com.softwarelab.dataextractor.core.domain.models.requests;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wilson
 * on Mon, 19/04/2021.
 */
@Data
@Builder
public class FileRequest {
    private String projectPath;
    private String nameUrl;
    @Builder.Default
    private List<String> libraries = new ArrayList<>();
}
