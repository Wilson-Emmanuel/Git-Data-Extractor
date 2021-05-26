package com.softwarelab.dataextractor.core.services;

import com.softwarelab.dataextractor.core.persistence.entities.ProjectEntity;
import com.softwarelab.dataextractor.core.persistence.models.dtos.ProjectModel;
import com.softwarelab.dataextractor.core.persistence.repositories.ProjectRepository;
import com.softwarelab.dataextractor.core.services.usecases.ProjectService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by Wilson
 * on Wed, 26/05/2021.
 */
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProjectServiceImpl implements ProjectService {

    ProjectRepository projectRepository;
    @Override
    public ProjectEntity saveProject(ProjectModel model) {
        Optional<ProjectEntity> optional = projectRepository.findByRemoteUrl(model.getRemoteUrl());
        if(optional.isPresent())
            return optional.get();

        ProjectEntity projectEntity = ProjectEntity.builder()
                .localPath(model.getLocalPath())
                .name(model.getName())
                .remoteUrl(model.getRemoteUrl())
                .build();
        return projectRepository.save(projectEntity);
    }

    @Override
    public boolean existsByRemoteUrl(String remoteUrl) {
        return projectRepository.existsByRemoteUrl(remoteUrl);
    }
}
