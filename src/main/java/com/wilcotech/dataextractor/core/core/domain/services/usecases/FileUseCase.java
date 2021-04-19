package com.wilcotech.dataextractor.core.core.domain.services.usecases;

import com.wilcotech.dataextractor.core.core.domain.models.FileModel;
import com.wilcotech.dataextractor.core.core.domain.models.PagedData;

import java.util.List;
import java.util.Set;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
public interface FileUseCase {
    PagedData<FileModel> getProjectFiles(Long projectId,int page, int size);
    List<FileModel> getAllDeveloperFiles(String developerName);//files created by developers
    boolean existsByUrlName(String nameUrl);
    Set<String> getAllLibraries(String fileName);
}
