package com.softwarelab.dataextractor.core.domain.services.usecases;

import com.softwarelab.dataextractor.core.domain.models.FileModel;
import com.softwarelab.dataextractor.core.domain.models.PagedData;
import com.softwarelab.dataextractor.core.domain.models.CommitModel;

import java.util.List;

/**
 * Created by Wilson
 * on Mon, 19/04/2021.
 */
public interface FileCommitUseCase {
    boolean existsByFileAndCommit(Long fileId, Long commitId);
    void save(Long fileId, Long commitId);
    PagedData<CommitModel> getAllCommitsInFile(Long fileId, int page, int size);
    List<FileModel> getAllFilesInCommit(Long commitId);
}
