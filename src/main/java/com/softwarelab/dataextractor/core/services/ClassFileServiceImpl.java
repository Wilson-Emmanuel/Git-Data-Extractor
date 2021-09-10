package com.softwarelab.dataextractor.core.services;

import com.softwarelab.dataextractor.core.persistence.entities.ClassFileEntity;
import com.softwarelab.dataextractor.core.persistence.entities.ProjectEntity;
import com.softwarelab.dataextractor.core.persistence.models.ClassFileObject;
import com.softwarelab.dataextractor.core.persistence.repositories.ClassFileRepository;
import com.softwarelab.dataextractor.core.persistence.repositories.ProjectRepository;
import com.softwarelab.dataextractor.core.services.usecases.ClassFileService;
import com.softwarelab.dataextractor.core.services.usecases.ProjectService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Wilson
 * on Thu, 02/09/2021.
 */
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClassFileServiceImpl implements ClassFileService {

    ProjectService projectService;
    ProjectRepository projectRepository;
    ClassFileRepository classFileRepository;

    @Override
    public List<ClassFileObject> projectClassFiles(Long projectId) {

        ProjectEntity projectEntity = projectRepository.findById(projectId).orElseThrow(()->new RuntimeException("Project not found"));
        List<ClassFileEntity> classFileEntities = classFileRepository.findAllByProject(projectEntity);

        return classFileEntities.stream().map(this::convertClassFileEntityToObject).collect(Collectors.toList());
    }

    @Override
    public ClassFileObject getClassFile(Long classFileId) {
        ClassFileEntity classFileEntity = classFileRepository.findById(classFileId).orElseThrow(()->new RuntimeException("Java class file not found"));
        return this.convertClassFileEntityToObject(classFileEntity);
    }

    private ClassFileObject convertClassFileEntityToObject(ClassFileEntity entity){
        return ClassFileObject.builder()
                .className(entity.getClassName())
                .project(projectService.getProject(entity.getProject().getId()))
                .id(entity.getId())
                .fullPath(entity.getFullPath())
                .build();
    }
}
