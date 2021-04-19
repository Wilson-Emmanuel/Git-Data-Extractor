package com.wilcotech.dataextractor.core.core.domain.services;

import com.wilcotech.dataextractor.core.core.domain.entities.ProjectEntity;
import com.wilcotech.dataextractor.core.core.domain.models.ProjectModel;
import com.wilcotech.dataextractor.core.core.domain.repositories.ProjectRepository;
import com.wilcotech.dataextractor.core.core.domain.services.usecases.ProjectUseCase;
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
    public ProjectModel save(ProjectModel projectModel) {
        ProjectEntity projectEntity = ProjectEntity.builder()
                .name(projectModel.getName())
                .remoteUrl(projectModel.getRemoteUrl())
                .localPath(projectModel.getLocalPath())
                .build();
        projectEntity = projectRepository.save(projectEntity);
        projectModel.setId(projectEntity.getId());
        return projectModel;
    }

    @Override
    public ProjectModel getProjectByName(String name) {
        Optional<ProjectEntity> projectEntity = projectRepository.findByName(name);
        return projectEntity.map(this::entityToProject).orElse(null);
    }

    @Override
    public ProjectModel getProjectByLocalPath(String path) {
        Optional<ProjectEntity> projectEntity = projectRepository.findByLocalPath(path);
        return projectEntity.isEmpty()?null:entityToProject(projectEntity.get());
    }

    @Override
    public List<ProjectModel> getAllProjects() {
        List<ProjectEntity> projectEntities = projectRepository.findAll();
        return projectEntities.stream()
                .map(this::entityToProject)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByNameOrLocalPath(String name, String localPath) {
        return projectRepository.existsByNameOrLocalPath(name,localPath);
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
