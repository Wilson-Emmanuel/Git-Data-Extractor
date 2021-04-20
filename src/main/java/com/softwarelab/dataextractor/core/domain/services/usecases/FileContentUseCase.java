package com.softwarelab.dataextractor.core.domain.services.usecases;

import com.softwarelab.dataextractor.core.domain.models.PagedData;
import com.softwarelab.dataextractor.core.domain.models.requests.FileContentRequest;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
public interface FileContentUseCase {
 String save(FileContentRequest fileContentRequest);
 PagedData<String> getProjectAllLibraries(Long projectId, int page, int size);
 PagedData<String> getProjectAllLibraries(String projectPath, int page, int size);
}
