package com.wilcotech.dataextractor.core.core.domain.services.usecases;

import com.wilcotech.dataextractor.core.core.domain.models.PagedData;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
public interface FileContentUseCase {
 PagedData<String> getAllLibraries(Long projectId, int page, int size);
}
