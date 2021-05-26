package com.softwarelab.dataextractor.core.services.usecases;

import com.softwarelab.dataextractor.core.persistence.entities.CommitEntity;
import com.softwarelab.dataextractor.core.persistence.entities.ProjectEntity;
import com.softwarelab.dataextractor.core.persistence.models.dtos.CommitModel;

import java.util.List;

/**
 * Created by Wilson
 * on Tue, 25/05/2021.
 */
public interface CommitService {
    CommitEntity saveCommit(CommitModel commitModel, ProjectEntity projectEntity);
    void saveCommitAll(List<CommitModel> commitModels, ProjectEntity projectEntity);
}
