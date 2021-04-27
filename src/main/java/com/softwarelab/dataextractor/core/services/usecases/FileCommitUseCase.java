package com.softwarelab.dataextractor.core.services.usecases;

import com.softwarelab.dataextractor.core.persistence.models.FileModel;
import com.softwarelab.dataextractor.core.persistence.models.PagedData;
import com.softwarelab.dataextractor.core.persistence.models.CommitModel;

import java.util.List;

/**
 * Created by Wilson
 * on Mon, 19/04/2021.
 */
public interface FileCommitUseCase {
    boolean existsByFileAndCommit(Long fileId, Long commitId);
    void save(Long fileId, String commitId);
    PagedData<CommitModel> getAllCommitsInFile(Long fileId, int page, int size);
    List<FileModel> getAllFilesInCommit(Long commitId);

}
