package com.softwarelab.dataextractor.core.services.usecases;

/**
 * Created by Wilson
 * on Wed, 21/04/2021.
 */
public interface FilePackageUseCase{
    boolean existsByPackageNameAndProject(String packageName, String projectPath);
    boolean existsFilePackageAndProjectLike(String library, String projectPath);
    boolean save(String projectPath, String packageName);
}
