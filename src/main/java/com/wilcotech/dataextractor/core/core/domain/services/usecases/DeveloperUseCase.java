package com.wilcotech.dataextractor.core.core.domain.services.usecases;

import com.wilcotech.dataextractor.core.core.domain.models.DeveloperModel;
import com.wilcotech.dataextractor.core.core.domain.models.PagedData;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
public interface DeveloperUseCase {
    boolean existsByName(String name);
    DeveloperModel getDeveloperByName(String name);
    DeveloperModel getDeveloperById(Long id);
    PagedData<DeveloperModel> getAllDeveloperByProject(Long projectId, int page, int size);
    PagedData<String> getAllLibrariesUsed(String name, int page, int size);
    PagedData<String> getAllLibrariesUsed(Long id, int page, int size);
}
