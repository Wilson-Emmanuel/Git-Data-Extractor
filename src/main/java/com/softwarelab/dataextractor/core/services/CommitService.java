package com.softwarelab.dataextractor.core.services;

import com.softwarelab.dataextractor.core.persistence.entities.DeveloperEntity;
import com.softwarelab.dataextractor.core.persistence.models.DeveloperModel;
import com.softwarelab.dataextractor.core.services.usecases.CommitUseCase;
import com.softwarelab.dataextractor.core.persistence.entities.CommitEntity;
import com.softwarelab.dataextractor.core.persistence.entities.ProjectEntity;
import com.softwarelab.dataextractor.core.persistence.models.CommitModel;
import com.softwarelab.dataextractor.core.persistence.models.PagedData;
import com.softwarelab.dataextractor.core.persistence.models.requests.CommitRequest;
import com.softwarelab.dataextractor.core.persistence.repositories.CommitRepository;
import com.softwarelab.dataextractor.core.persistence.repositories.DeveloperRepository;
import com.softwarelab.dataextractor.core.persistence.repositories.ProjectRepository;
import com.softwarelab.dataextractor.core.utilities.DateTimeUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
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
public class CommitService implements CommitUseCase {

    CommitRepository commitRepository;
    ProjectRepository projectRepository;
    DeveloperRepository developerRepository;


    @Override
    public int batchSave(List<CommitRequest> commitRequests, String projectPath) {
        ProjectEntity projectEntity = projectRepository.findByLocalPath(projectPath).orElse(null);
        if(projectEntity == null)
            return 0;

        List<CommitEntity> commitEntities  = new ArrayList<>();
        DeveloperEntity developerEntity;
        CommitEntity commitEntity;

        for(CommitRequest commitRequest: commitRequests){
             developerEntity = developerRepository.findByNameAndEmailAndProject_LocalPath(commitRequest.getDeveloperName(),commitRequest.getDeveloperEmail(),commitRequest.getProjectPath())
                    .orElseGet(()-> developerRepository.save(
                            DeveloperEntity.builder()
                            .name(commitRequest.getDeveloperName())
                            .email(commitRequest.getDeveloperEmail())
                            .project(projectEntity)
                            .build()
                    ));
             commitEntity = CommitEntity.builder()
                     .commitId(commitRequest.getCommitId())
                     .commitDate(DateTimeUtil.getInstantTime(commitRequest.getCommitDate()))
                     .developer(developerEntity)
                     .build();
             commitEntities.add(commitEntity);
        }
        if(!commitEntities.isEmpty()){
            commitEntities = commitRepository.saveAll(commitEntities);
        }
        return commitEntities.size();
    }

    @Override
    public CommitModel save(CommitRequest commitRequest) {
        CommitEntity commitEntity = commitRepository.findByCommitId(commitRequest.getCommitId()).orElse(null);
        if(commitEntity != null)
            return entityToCommit(commitEntity);

        Optional<DeveloperEntity> optionalDeveloperEntity = developerRepository.findByNameAndEmailAndProject_LocalPath(commitRequest.getDeveloperName(),
                commitRequest.getDeveloperEmail(),commitRequest.getProjectPath());
        if(optionalDeveloperEntity.isEmpty())
            return null;

         commitEntity = CommitEntity.builder()
                .commitDate(DateTimeUtil.getInstantTime(commitRequest.getCommitDate()))
                .commitId(commitRequest.getCommitId())
                .developer(optionalDeveloperEntity.get())
                .build();
        return entityToCommit(commitRepository.save(commitEntity));
    }

    @Override
    public boolean existsByCommitId(String commitId) {
         return commitRepository.existsByCommitId(commitId);
    }

    @Override
    public CommitModel getCommitByCommitId(String commitId) {
        return commitRepository.findByCommitId(commitId).map(this::entityToCommit).orElse(null);
    }

    @Override
    public CommitModel getCommitByCommitId(Long id) {
        return commitRepository.findById(id).map(this::entityToCommit).orElse(null);
    }

    @Override
    public List<CommitModel> getAllProjectCommit(Long projectId) {
        Optional<ProjectEntity> projectEntity = projectRepository.findById(projectId);
        if(projectEntity.isEmpty())
            return Collections.emptyList();
        return commitRepository.findAllByDeveloper_Project(projectEntity.get())
                .stream().map(this::entityToCommit)
                .collect(Collectors.toList());
    }

    @Override
    public PagedData<CommitModel> getAllProjectCommit(Long projectId, int size, int page) {
        Optional<ProjectEntity> projectEntity = projectRepository.findById(projectId);
        if(projectEntity.isEmpty())
            return new PagedData<>(Collections.emptyList(),0,0);

        Pageable pageable = PageRequest.of(size,page, Sort.Direction.DESC,"commitDate");
        Page<CommitEntity> commitEntityPage = commitRepository.findAllByDeveloper_Project(projectEntity.get(),pageable);
        return new PagedData<>(
                commitEntityPage.stream().map(this::entityToCommit).collect(Collectors.toList()),
                commitEntityPage.getTotalElements(),
                commitEntityPage.getTotalPages()
                );
    }

    @Override
    public List<CommitModel> getAllAuthorCommit(String name,String email, String projectPath) {
        Optional<DeveloperEntity> optionalDeveloperEntity = developerRepository.findByNameAndEmailAndProject_LocalPath(name,email,projectPath);
        if(optionalDeveloperEntity.isEmpty())
            return Collections.emptyList();

        return commitRepository.findAllByDeveloper(optionalDeveloperEntity.get()).stream()
                .map(this::entityToCommit).collect(Collectors.toList());
    }

    @Override
    public PagedData<CommitModel> getAllAuthorCommit(String name,String email, String projectPath, int size, int page) {
        Optional<DeveloperEntity> optionalDeveloperEntity = developerRepository.findByNameAndEmailAndProject_LocalPath(name,email,projectPath);
        if(optionalDeveloperEntity.isEmpty())
            return new PagedData<>(Collections.emptyList(),0,0);

        Pageable pageable = PageRequest.of(size,page, Sort.Direction.DESC,"commitDate");
        Page<CommitEntity> commitEntityPage = commitRepository.findAllByDeveloper(optionalDeveloperEntity.get(),pageable);

        return new PagedData<>(
                commitEntityPage.stream().map(this::entityToCommit).collect(Collectors.toList()),
                commitEntityPage.getTotalElements(),
                commitEntityPage.getTotalPages()
        );
    }
    @Override
    public CommitModel entityToCommit(CommitEntity commitEntity){
        DeveloperModel developerModel = DeveloperModel.builder()
                .name(commitEntity.getDeveloper().getName())
                .email(commitEntity.getDeveloper().getEmail())
                .build();
        return CommitModel.builder()
                .id(commitEntity.getId())
                .commitId(commitEntity.getCommitId())
                .developer(developerModel)
                .commitDate(DateTimeUtil.getDateTime(commitEntity.getCommitDate()))
                .build();
    }
}
