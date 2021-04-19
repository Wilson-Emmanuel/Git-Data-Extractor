package com.wilcotech.dataextractor.core.core.domain.services;

import com.wilcotech.dataextractor.core.core.domain.entities.CommitEntity;
import com.wilcotech.dataextractor.core.core.domain.entities.DeveloperEntity;
import com.wilcotech.dataextractor.core.core.domain.entities.ProjectEntity;
import com.wilcotech.dataextractor.core.core.domain.models.CommitModel;
import com.wilcotech.dataextractor.core.core.domain.models.DeveloperModel;
import com.wilcotech.dataextractor.core.core.domain.models.PagedData;
import com.wilcotech.dataextractor.core.core.domain.repositories.CommitRepository;
import com.wilcotech.dataextractor.core.core.domain.repositories.DeveloperRepository;
import com.wilcotech.dataextractor.core.core.domain.repositories.ProjectRepository;
import com.wilcotech.dataextractor.core.core.domain.services.usecases.CommitUseCase;
import com.wilcotech.dataextractor.core.core.utilities.DateTimeUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
        return commitRepository.findAllByProject(projectEntity.get())
                .stream().map(this::entityToCommit)
                .collect(Collectors.toList());
    }

    @Override
    public PagedData<CommitModel> getAllProjectCommit(Long projectId, int size, int page) {
        Optional<ProjectEntity> projectEntity = projectRepository.findById(projectId);
        if(projectEntity.isEmpty())
            return new PagedData<>(Collections.emptyList(),0,0);

        Pageable pageable = PageRequest.of(size,page, Sort.Direction.DESC,"commitDate");
        Page<CommitEntity> commitEntityPage = commitRepository.findAllByProject(projectEntity.get(),pageable);
        return new PagedData<>(
                commitEntityPage.stream().map(this::entityToCommit).collect(Collectors.toList()),
                commitEntityPage.getTotalElements(),
                commitEntityPage.getTotalPages()
                );
    }

    @Override
    public List<CommitModel> getAllAuthorCommit(String name) {
        Optional<DeveloperEntity> optionalDeveloperEntity = developerRepository.findByName(name);
        if(optionalDeveloperEntity.isEmpty())
            return Collections.emptyList();
        return commitRepository.findAllByDeveloper(optionalDeveloperEntity.get()).stream()
                .map(this::entityToCommit).collect(Collectors.toList());
    }

    @Override
    public PagedData<CommitModel> getAllAuthorCommit(String name, int size, int page) {
        Optional<DeveloperEntity> optionalDeveloperEntity = developerRepository.findByName(name);
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
    private CommitModel entityToCommit(CommitEntity commitEntity){
        //Optional<ProjectEntity> projectEntity = projectRepository.findById(commitEntity.getId());
        DeveloperModel developerModel = DeveloperModel.builder()
                .name(commitEntity.getDeveloper().getName())
                .email(commitEntity.getDeveloper().getEmail())
                .build();
        return CommitModel.builder()
                .id(commitEntity.getId())
                .commitId(commitEntity.getCommitId())
                .developer(developerModel)
                //.projectName(projectEntity.map(ProjectEntity::getName).orElse(null))
                .commitDate(DateTimeUtil.getDateTime(commitEntity.getCommitDate()))
                .build();
    }
}
