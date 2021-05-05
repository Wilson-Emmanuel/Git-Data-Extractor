package com.softwarelab.dataextractor.core.services;

import com.softwarelab.dataextractor.core.persistence.entities.ProjectEntity;
import com.softwarelab.dataextractor.core.persistence.models.ProjectModel;
import com.softwarelab.dataextractor.core.persistence.models.requests.ProjectRequest;
import com.softwarelab.dataextractor.core.persistence.repositories.ProjectRepository;
import com.softwarelab.dataextractor.core.services.usecases.ProjectUseCase;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProjectService implements ProjectUseCase {

    ProjectRepository projectRepository;

    @Override
    public ProjectModel save(ProjectRequest projectRequest) {
        ProjectEntity projectEntity = projectRepository.findByLocalPath(projectRequest.getLocalPath())
                .orElseGet(() -> projectRepository.save(ProjectEntity.builder()
                        .name(projectRequest.getName())
                        .remoteUrl(projectRequest.getRemoteUrl())
                        .localPath(projectRequest.getLocalPath())
                        .build()));
       return entityToProject(projectEntity);
    }

    @Override
    public ProjectModel getProjectByLocalPath(String path) {
        Optional<ProjectEntity> optionalProjectEntity = projectRepository.findByLocalPath(path);
        return optionalProjectEntity.map(this::entityToProject).orElse(null);
    }

    @Override
    public List<ProjectModel> getAllProjects() {
        List<ProjectEntity> projectEntities = projectRepository.findAll();
        return projectEntities.stream()
                .map(this::entityToProject)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByLocalPath(String localPath) {
        return projectRepository.existsByLocalPath(localPath);
    }

    @Override
    public boolean existsByRemoteUrl(String remoteUrl) {
        return projectRepository.existsByRemoteUrl(remoteUrl);
    }

    private ProjectModel entityToProject(ProjectEntity projectEntity){
        return ProjectModel.builder()
                .name(projectEntity.getName())
                .localPath(projectEntity.getLocalPath())
                .remoteUrl(projectEntity.getRemoteUrl())
                .id(projectEntity.getId())
                .build();

    }
}
