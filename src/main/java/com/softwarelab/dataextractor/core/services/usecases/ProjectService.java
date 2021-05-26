package com.softwarelab.dataextractor.core.services.usecases;

import com.softwarelab.dataextractor.core.persistence.entities.ProjectEntity;
import com.softwarelab.dataextractor.core.persistence.models.dtos.ProjectModel;

/**
 * Created by Wilson
 * on Wed, 26/05/2021.
 */
public interface ProjectService {
    ProjectEntity saveProject(ProjectModel model);
    boolean existsByRemoteUrl(String remoteUrl);
}
