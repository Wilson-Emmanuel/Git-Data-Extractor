package com.wilcotech.dataextractor.core.core.domain.services.usecases;

import com.wilcotech.dataextractor.core.core.domain.models.CommitModel;
import com.wilcotech.dataextractor.core.core.domain.models.PagedData;

import java.util.List;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
public interface CommitUseCase {
    boolean existsByCommitId(String commitId);
    CommitModel getCommitByCommitId(String commitId);
    CommitModel getCommitByCommitId(Long id);
    List<CommitModel> getAllProjectCommit(Long projectId);
    PagedData<CommitModel> getAllProjectCommit(Long project, int size, int page);
    List<CommitModel> getAllAuthorCommit(String name);
    PagedData<CommitModel> getAllAuthorCommit(String name, int size, int page);
}
