package com.softwarelab.dataextractor.core.domain.services.usecases;

import com.softwarelab.dataextractor.core.domain.models.FileModel;
import com.softwarelab.dataextractor.core.domain.entities.FileEntity;
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
    PagedData<FileModel> getProjectFiles(Long projectId,int page, int size);
    PagedData<FileModel> getProjectFiles(String projectPath,int page, int size);
    List<FileModel> getAllDeveloperFiles(String developerName,String email,String projectPath);//files created by developers
    boolean existsByNameUrlAndProject(String nameUrl, String projectPath);
    Set<String> getAllLibrariesByNameUrlAndProject(String nameUrl, String projectPath);
    FileModel entityToFile(FileEntity fileEntity);
}
