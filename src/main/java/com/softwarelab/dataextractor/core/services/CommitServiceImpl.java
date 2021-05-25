package com.softwarelab.dataextractor.core.services;

import com.softwarelab.dataextractor.core.persistence.entities.CommitEntity;
import com.softwarelab.dataextractor.core.persistence.entities.LibraryEntity;
import com.softwarelab.dataextractor.core.persistence.models.dtos.CommitModel;
import com.softwarelab.dataextractor.core.persistence.repositories.CommitRepository;
import com.softwarelab.dataextractor.core.persistence.repositories.LibraryRepository;
import com.softwarelab.dataextractor.core.services.usecases.CommitService;
import com.softwarelab.dataextractor.core.utilities.DateTimeUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Wilson
 * on Tue, 25/05/2021.
 */
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommitServiceImpl implements CommitService {

    CommitRepository commitRepository;
    LibraryRepository libraryRepository;

    @Override
    public CommitEntity saveCommit(CommitModel commitModel) {
        CommitEntity commitEntity = CommitEntity.builder()
                .commitId(commitModel.getCommitId())
                .commitDate(DateTimeUtil.getInstantTime(commitModel.getCommitDate()))
                .developerName(commitModel.getDeveloperName())
                .developerEmail(commitModel.getDeveloperEmail())
                .fileUrl(commitModel.getFileUrl())
                .build();
        return commitRepository.save(commitEntity);

    }

    @Override
    public void saveCommitAll(List<CommitModel> commitModels) {
        List<LibraryEntity> libs = new ArrayList<>();

        for(CommitModel commitModel: commitModels){
            final CommitEntity commitEntity = saveCommit(commitModel);

            libs.addAll(commitModel.getLibraries().stream()
                    .map(lib->LibraryEntity.builder()
                            .commit(commitEntity)
                            .library(lib)
                            .build()).collect(Collectors.toList()));
        }
        libraryRepository.saveAll(libs);
    }
}
