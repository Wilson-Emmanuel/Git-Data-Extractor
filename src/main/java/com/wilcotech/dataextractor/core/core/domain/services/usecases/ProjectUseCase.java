package com.wilcotech.dataextractor.core.core.domain.services.usecases;

import com.wilcotech.dataextractor.core.core.domain.models.ProjectModel;

import java.util.List;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
public interface ProjectUseCase {
    ProjectModel save(ProjectModel projectModel);
    ProjectModel getProjectByName(String name);
    ProjectModel getProjectByLocalPath(String path);
    List<ProjectModel> getAllProjects();
    boolean existsByNameOrLocalPath(String name, String localPath);
}
