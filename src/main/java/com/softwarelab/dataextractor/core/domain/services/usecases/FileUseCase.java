package com.softwarelab.dataextractor.core.domain.services.usecases;

import com.softwarelab.dataextractor.core.domain.entities.FileEntity;
import com.softwarelab.dataextractor.core.domain.models.FileCountModel;
import com.softwarelab.dataextractor.core.domain.models.FileModel;
import com.softwarelab.dataextractor.core.domain.models.PagedData;
import com.softwarelab.dataextractor.core.domain.models.requests.FileRequest;

import java.util.List;
import java.util.Set;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
public interface FileUseCase {
    FileModel save(FileRequest fileRequest);
    FileCountModel saveBatch(List<FileRequest> fileRequestList);
    PagedData<FileModel> getProjectFiles(Long projectId,int page, int size);
    PagedData<FileModel> getProjectFiles(String projectPath,int page, int size);
    boolean existsByNameUrlAndProject(String nameUrl, String projectPath);
    Set<String> getAllLibrariesByNameUrlAndProject(String nameUrl, String projectPath);
    FileModel entityToFile(FileEntity fileEntity);
}
