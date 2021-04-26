package com.softwarelab.dataextractor.core.persistence.services.usecases;

import com.softwarelab.dataextractor.core.persistence.models.ProjectModel;
import com.softwarelab.dataextractor.core.persistence.models.requests.ProjectRequest;

import java.util.List;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
public interface ProjectUseCase {
    ProjectModel save(ProjectRequest projectRequest);
    ProjectModel getProjectByLocalPath(String path);
    List<ProjectModel> getAllProjects();
    boolean existsByLocalPath(String localPath);
    boolean existsByRemoteUrl(String remoteUrl);
}
