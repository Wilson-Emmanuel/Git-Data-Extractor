package com.wilcotech.dataextractor.core.core.domain.services.usecases;

import com.wilcotech.dataextractor.core.core.domain.models.PagedData;

import java.util.Set;

/**
 * Created by Wilson
 * on Mon, 19/04/2021.
 */
public interface FileCommitAndContentUseCase {
    PagedData<String> getDevelopersLibraries(String name, int page, int size);
    PagedData<String> getDevelopersLibraries(Long developerId, int page, int size);
    PagedData<String> getCommitLibraries(String commitId, int page, int size);
    Set<String> getCommitFileLibraries(String commitId, Long fileId);

}
