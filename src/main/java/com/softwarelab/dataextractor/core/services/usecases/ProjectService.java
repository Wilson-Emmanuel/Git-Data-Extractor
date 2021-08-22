package com.softwarelab.dataextractor.core.services.usecases;

import com.softwarelab.dataextractor.core.persistence.models.ProjectObject;

import java.util.List;

/**
 * Created by Wilson
 * on Wed, 26/05/2021.
 */
public interface ProjectService {
    ProjectObject saveProject(ProjectObject model);
    boolean existsByRemoteUrl(String remoteUrl);
    boolean existsByLocalPath(String localPath);
    boolean isLocalPathValid(String localPath);
    boolean isValidRemoteURL(String remoteURL);
    List<ProjectObject> getAllProjects();
}
