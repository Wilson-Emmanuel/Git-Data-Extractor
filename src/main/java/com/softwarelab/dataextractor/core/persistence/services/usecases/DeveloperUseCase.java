package com.softwarelab.dataextractor.core.persistence.services.usecases;

import com.softwarelab.dataextractor.core.persistence.models.DeveloperModel;
import com.softwarelab.dataextractor.core.persistence.models.PagedData;
import com.softwarelab.dataextractor.core.persistence.models.requests.DeveloperRequest;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
public interface DeveloperUseCase {
    DeveloperModel save(DeveloperRequest developerRequest);
    boolean existsByNameAndEmailAndProject(String name,String email, String projectPath);
    DeveloperModel getDeveloperByNameAndEmailAndProject(String name, String email, String projectPath);
    DeveloperModel getDeveloperById(Long id);
    PagedData<DeveloperModel> getAllDeveloperByProject(Long projectId, int page, int size);
    PagedData<DeveloperModel> getAllDeveloperByProject(String projectPath, int page, int size);
    PagedData<String> getAllLibrariesUsed(String name, String email, String projectPath, int page, int size);
    PagedData<String> getAllLibrariesUsed(Long id, int page, int size);
}
