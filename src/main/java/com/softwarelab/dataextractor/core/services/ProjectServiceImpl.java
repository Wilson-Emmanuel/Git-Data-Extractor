package com.softwarelab.dataextractor.core.services;

import com.softwarelab.dataextractor.core.persistence.entities.ProjectEntity;
import com.softwarelab.dataextractor.core.persistence.models.ProjectObject;
import com.softwarelab.dataextractor.core.persistence.repositories.ProjectRepository;
import com.softwarelab.dataextractor.core.services.usecases.ProjectService;
import com.softwarelab.dataextractor.core.services.processors.CMDProcessor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Wilson
 * on Wed, 26/05/2021.
 */
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProjectServiceImpl implements ProjectService {

    ProjectRepository projectRepository;
    CMDProcessor cmdProcessor;

    @Override
    public ProjectObject saveProject(ProjectObject model) {
        Optional<ProjectEntity> optional = projectRepository.findByRemoteUrl(model.getRemoteURL());
        if(optional.isPresent())
            return convertEntityToModel(optional.get());

        ProjectEntity projectEntity = ProjectEntity.builder()
                .localPath(model.getLocalPath())
                .name(model.getName())
                .remoteUrl(model.getRemoteURL())
                .build();
        projectEntity = projectRepository.save(projectEntity);
        return this.convertEntityToModel(projectEntity);
    }

    @Override
    public boolean existsByRemoteUrl(String remoteUrl) {
        return projectRepository.existsByRemoteUrl(remoteUrl);
    }


    @Override
    public boolean existsByLocalPath(String localPath) {
        return projectRepository.existsByLocalPath(localPath);
    }

    @Override
    public boolean isLocalPathValid(String localPath) {
        return cmdProcessor.isValidDir(localPath);
    }

    @Override
    public boolean isValidRemoteURL(String remoteURL) {
        return false;
    }

    @Override
    public List<ProjectObject> getAllProjects() {
        return projectRepository.findAll().stream().map(this::convertEntityToModel)
                .collect(Collectors.toList());
    }
    private ProjectObject convertEntityToModel(ProjectEntity projectEntity){
        return ProjectObject.builder()
                .id(projectEntity.getId())
                .name(projectEntity.getName())
                .remoteURL(projectEntity.getRemoteUrl())
                .localPath(projectEntity.getLocalPath())
                .build();
    }
}
