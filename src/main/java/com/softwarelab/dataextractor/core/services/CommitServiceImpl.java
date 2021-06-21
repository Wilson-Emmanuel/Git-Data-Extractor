package com.softwarelab.dataextractor.core.services;

import com.softwarelab.dataextractor.core.persistence.entities.CommitEntity;
import com.softwarelab.dataextractor.core.persistence.entities.LibraryEntity;
import com.softwarelab.dataextractor.core.persistence.entities.ProjectEntity;
import com.softwarelab.dataextractor.core.persistence.models.PagedData;
import com.softwarelab.dataextractor.core.persistence.models.dtos.CommitModel;
import com.softwarelab.dataextractor.core.persistence.repositories.CommitRepository;
import com.softwarelab.dataextractor.core.persistence.repositories.LibraryRepository;
import com.softwarelab.dataextractor.core.persistence.repositories.ProjectRepository;
import com.softwarelab.dataextractor.core.services.usecases.CommitService;
import com.softwarelab.dataextractor.core.utilities.DateTimeUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
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
    ProjectRepository projectRepository;

    @Override
    public CommitEntity saveCommit(CommitModel commitModel,ProjectEntity projectEntity) {
        CommitEntity commitEntity = CommitEntity.builder()
                .commitId(commitModel.getCommitId())
                .commitDate(DateTimeUtil.getInstantTime(commitModel.getCommitDate()))
                .developerName(commitModel.getDeveloperName())
                .developerEmail(commitModel.getDeveloperEmail())
                .fileUrl(commitModel.getFileUrl())
                .project(projectEntity)
                .build();
        return commitRepository.save(commitEntity);

    }

    @Override
    public void saveCommitAll(List<CommitModel> commitModels, ProjectEntity projectEntity) {

        List<LibraryEntity> libs = new ArrayList<>();

        for(CommitModel commitModel: commitModels){
            if(commitRepository.existsByCommitIdAndProjectAndDeveloperName(commitModel.getCommitId(), projectEntity, commitModel.getDeveloperName()))
                continue;

            final CommitEntity commitEntity = saveCommit(commitModel,projectEntity);

            Map<String,Boolean> devLibs = getDeveloperLibs(commitModel.getDeveloperName(),projectEntity);

            //exclude all libs the dev already have
            libs.addAll(commitModel.getLibraries()
                    .stream()
                    .filter(lib-> devLibs.getOrDefault(lib,true))
                    .map(lib->LibraryEntity.builder()
                            .commit(commitEntity)
                            .library(lib)
                            .build()).collect(Collectors.toList()));
        }
        if(!libs.isEmpty())
            libraryRepository.saveAll(libs);
    }

    @Override
    public PagedData<CommitModel> getCommits(Long projectId, int page, int size) {
        Optional<ProjectEntity> optionalProjectEntity = projectRepository.findById(projectId);
        if(optionalProjectEntity.isEmpty()){
            return new PagedData<>(Collections.emptyList(),0,0);
        }
        Pageable pageable = PageRequest.of(page,size);
        Page<CommitEntity> commitEntityPage = commitRepository.findAllByProject(optionalProjectEntity.get(),pageable);
        List<CommitModel> commitModels = commitEntityPage.stream().map(this::convertEntityToModel).collect(Collectors.toList());
        return new PagedData<>(commitModels,commitEntityPage.getTotalElements(),commitEntityPage.getTotalPages());
    }

    @Override
    public List<String> getDevelopers(Long projectId) {
        var optionalProjectEntity = projectRepository.findById(projectId);
        if(optionalProjectEntity.isEmpty())
            return Collections.emptyList();

        return commitRepository.getDistinctDevelopers(optionalProjectEntity.get());
    }

    private Map<String,Boolean> getDeveloperLibs(String developerName, ProjectEntity project){
        Map<String,Boolean> res = new HashMap<>();
        List<CommitEntity> commitEntityList = commitRepository.findAllByDeveloperNameAndProject(developerName, project);
        for(CommitEntity commitEntity: commitEntityList) {
            List<LibraryEntity> libraryEntities = libraryRepository.findAllByCommit(commitEntity);
            for (LibraryEntity lib : libraryEntities) {
                res.put(lib.getLibrary(),false);
            }
        }
        return res;
    }

    @Override
    public Optional<String> getDeveloperLibraries(String developerName, Long projectId) {
        var optionalProjectEntity = projectRepository.findById(projectId);
        if(optionalProjectEntity.isEmpty())
            return Optional.empty();

        StringBuilder sb = null;
        List<CommitEntity> commitEntityList = commitRepository.findAllByDeveloperNameAndProject(developerName,optionalProjectEntity.get());
        for(CommitEntity commitEntity: commitEntityList){
            List<LibraryEntity> libraryEntities = libraryRepository.findAllByCommit(commitEntity);
            for(LibraryEntity lib: libraryEntities){
                if(sb == null){
                    sb = new StringBuilder();
                    sb.append(lib.getLibrary());
                }else{
                    sb.append(",");
                    sb.append(lib.getLibrary());
                }
            }
        }
        if(sb == null)return Optional.empty();
        return Optional.of(sb.toString());
    }

    private CommitModel convertEntityToModel(CommitEntity commitEntity){
        return CommitModel.builder()
                .commitId(commitEntity.getCommitId())
                .commitDate(DateTimeUtil.isoDateTime(commitEntity.getCommitDate()))
                .developerEmail(commitEntity.getDeveloperEmail())
                .developerName(commitEntity.getDeveloperName())
                .fileUrl(commitEntity.getFileUrl())
                .libraries(getLibraries(commitEntity))
                .build();
    }

    private Set<String> getLibraries(CommitEntity commitEntity){
        return libraryRepository.findAllByCommit(commitEntity).stream().map(LibraryEntity::getLibrary)
                .collect(Collectors.toSet());
    }


}
