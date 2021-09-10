package com.softwarelab.dataextractor.core.services.usecases;

import com.softwarelab.dataextractor.core.persistence.models.LibraryObject;
import com.softwarelab.dataextractor.core.persistence.models.PagedData;

import java.util.Set;

/**
 * Created by Wilson
 * on Thu, 02/09/2021.
 */
public interface LibraryService {
    Set<LibraryObject> getCommitLibraries(Long commitId);
    PagedData<LibraryObject> getAllUniqueLibraries(int page, int size);
    PagedData<LibraryObject> getUnclassifiedLibraries(int page, int size);

    void updateLibrary(String name, String provider, String category);

    Set<LibraryObject> getUniqueProjectLibraries(Long projectId);
    Set<LibraryObject> getUnclassifiedUniqueProjectLibraries(Long projectId);
}
