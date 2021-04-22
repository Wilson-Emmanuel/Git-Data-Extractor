package com.softwarelab.dataextractor.core.domain.services.usecases;

import com.softwarelab.dataextractor.core.domain.models.PagedData;
import com.softwarelab.dataextractor.core.domain.models.requests.CommitAndContentRequest;

import java.util.List;
import java.util.Set;

/**
 * Created by Wilson
 * on Mon, 19/04/2021.
 */
public interface CommitAndContentUseCase {
    String save(CommitAndContentRequest commitAndContentRequest);
    int saveBatchPerFile(List<CommitAndContentRequest> commitAndContentRequests);
    PagedData<String> getDevelopersLibraries(String name, String email, String project, int page, int size);
    PagedData<String> getDevelopersLibraries(Long developerId, int page, int size);
    PagedData<String> getCommitLibraries(String commitId, int page, int size);
    Set<String> getCommitFileLibraries(String commitId, Long fileContentId);
    boolean existsByCommitAndFileContentId(String commitId, Long fileContentId);

}
