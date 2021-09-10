package com.softwarelab.dataextractor.core.services.usecases;

import com.softwarelab.dataextractor.core.persistence.models.ClassFileObject;

import java.util.List;

/**
 * Created by Wilson
 * on Thu, 02/09/2021.
 */
public interface ClassFileService {
    List<ClassFileObject> projectClassFiles(Long projectId);
    ClassFileObject getClassFile(Long classFileId);
}
