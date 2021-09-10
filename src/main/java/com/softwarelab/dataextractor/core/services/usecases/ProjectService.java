package com.softwarelab.dataextractor.core.services.usecases;

import com.softwarelab.dataextractor.core.persistence.models.CommitObject;
import com.softwarelab.dataextractor.core.persistence.models.CommiterObject;
import com.softwarelab.dataextractor.core.persistence.models.ProjectObject;
import com.softwarelab.dataextractor.core.persistence.models.dtos.FileModel;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
    Optional<ProjectObject> getByLocalPath(String urlPath);
    List<ProjectObject> getAllProjects();
    Set<String> saveFiles(Map<String,Boolean> packages, FileModel fileModel, ProjectObject projectObject);
    List<String> getLibraries(String fileFullPath, Long projectId);

    ProjectObject getProject(Long projectId);

    //Fetch data
   //List<CommitObject>
}
