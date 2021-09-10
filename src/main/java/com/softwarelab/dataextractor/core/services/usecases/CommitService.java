package com.softwarelab.dataextractor.core.services.usecases;

import com.softwarelab.dataextractor.core.persistence.models.CommitObject;
import com.softwarelab.dataextractor.core.persistence.models.PagedData;
import com.softwarelab.dataextractor.core.persistence.models.dtos.CommitModel;

import java.util.List;

/**
 * Created by Wilson
 * on Tue, 25/05/2021.
 */
public interface CommitService {

    void saveCommits(CommitModel commitModel, String fileUrl, Long projectId);

    CommitObject getCommit(Long id);
    CommitObject getCommit(String commitId);
    List<CommitObject> getFileCommits(Long classFileId);
    PagedData<CommitObject> getProjectCommits(Long projectId, int page, int size);
    PagedData<CommitObject> getAllProjectCommits(int page, int size);

}
