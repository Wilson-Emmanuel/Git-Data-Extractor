package com.softwarelab.dataextractor.core.persistence.services.usecases;

import com.softwarelab.dataextractor.core.persistence.entities.CommitEntity;
import com.softwarelab.dataextractor.core.persistence.models.CommitModel;
import com.softwarelab.dataextractor.core.persistence.models.PagedData;
import com.softwarelab.dataextractor.core.persistence.models.requests.CommitRequest;

import java.util.List;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
public interface CommitUseCase {
    int batchSave(List<CommitRequest> commitRequests, String projectPath);
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
