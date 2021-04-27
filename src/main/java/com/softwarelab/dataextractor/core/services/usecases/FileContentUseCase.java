package com.softwarelab.dataextractor.core.services.usecases;

import com.softwarelab.dataextractor.core.persistence.models.PagedData;
import com.softwarelab.dataextractor.core.persistence.models.requests.FileContentRequest;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
public interface FileContentUseCase {
 String save(FileContentRequest fileContentRequest);
 PagedData<String> getProjectAllLibraries(Long projectId, int page, int size);
 PagedData<String> getProjectAllLibraries(String projectPath, int page, int size);
}
