package com.softwarelab.dataextractor.core.persistence.services;

import com.softwarelab.dataextractor.core.persistence.entities.FilePackageEntity;
import com.softwarelab.dataextractor.core.persistence.entities.ProjectEntity;
import com.softwarelab.dataextractor.core.persistence.repositories.FilePackageRepository;
import com.softwarelab.dataextractor.core.persistence.repositories.ProjectRepository;
import com.softwarelab.dataextractor.core.persistence.services.usecases.FilePackageUseCase;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by Wilson
 * on Wed, 21/04/2021.
 */
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilePackageService implements FilePackageUseCase {

    FilePackageRepository filePackageRepository;
    ProjectRepository projectRepository;

    @Override
    public boolean existsByPackageNameAndProject(String packageName, String projectPath) {
        return filePackageRepository.existsByPackageNameAndProject_LocalPath(packageName,projectPath);
    }

    @Override
    public boolean existsFilePackageAndProjectLike(String library, String projectPath) {
        return filePackageRepository.existsFilePackageLike(library,projectPath);
    }

    @Override
    public boolean save(String projectPath, String packageName) {
        if(filePackageRepository.existsByPackageNameAndProject_LocalPath(packageName,projectPath))
            return true;

        Optional<ProjectEntity> optionalProjectEntity = projectRepository.findByLocalPath(projectPath);
        if(optionalProjectEntity.isEmpty())
            return false;

        FilePackageEntity filePackageEntity = FilePackageEntity.builder()
                .packageName(packageName)
                .project(optionalProjectEntity.get())
                .build();
        return filePackageRepository.save(filePackageEntity).getId() > 0L;
    }
}
