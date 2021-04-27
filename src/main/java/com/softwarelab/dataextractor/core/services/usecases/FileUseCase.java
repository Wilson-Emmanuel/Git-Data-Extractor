package com.softwarelab.dataextractor.core.services.usecases;

import com.softwarelab.dataextractor.core.persistence.entities.FileEntity;
import com.softwarelab.dataextractor.core.persistence.models.FileCountModel;
import com.softwarelab.dataextractor.core.persistence.models.FileModel;
import com.softwarelab.dataextractor.core.persistence.models.PagedData;
import com.softwarelab.dataextractor.core.persistence.models.requests.FileRequest;

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
