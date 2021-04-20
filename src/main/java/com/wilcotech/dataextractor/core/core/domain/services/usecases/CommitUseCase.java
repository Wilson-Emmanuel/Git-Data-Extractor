package com.wilcotech.dataextractor.core.core.domain.services.usecases;

import com.wilcotech.dataextractor.core.core.domain.entities.CommitEntity;
import com.wilcotech.dataextractor.core.core.domain.models.CommitModel;
import com.wilcotech.dataextractor.core.core.domain.models.PagedData;
import com.wilcotech.dataextractor.core.core.domain.models.requests.CommitRequest;

import java.util.List;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
public interface CommitUseCase {
    CommitModel save(CommitRequest commitRequest);
    boolean existsByCommitId(String commitId);
    CommitModel getCommitByCommitId(String commitId);
    CommitModel entityToCommit(CommitEntity commitEntity);
    CommitModel getCommitByCommitId(Long id);
    List<CommitModel> getAllProjectCommit(Long projectId);
    PagedData<CommitModel> getAllProjectCommit(Long project, int size, int page);
    List<CommitModel> getAllAuthorCommit(String name,String email,String projectPath);
    PagedData<CommitModel> getAllAuthorCommit(String name, String email,String projectPath, int size, int page);
}
