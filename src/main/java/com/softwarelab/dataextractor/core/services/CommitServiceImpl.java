package com.softwarelab.dataextractor.core.services;

import com.softwarelab.dataextractor.core.persistence.entities.*;
import com.softwarelab.dataextractor.core.persistence.models.CommitObject;
import com.softwarelab.dataextractor.core.persistence.models.PagedData;
import com.softwarelab.dataextractor.core.persistence.models.dtos.CommitModel;
import com.softwarelab.dataextractor.core.persistence.repositories.*;
import com.softwarelab.dataextractor.core.services.usecases.ClassFileService;
import com.softwarelab.dataextractor.core.services.usecases.CommitService;
import com.softwarelab.dataextractor.core.services.usecases.CommiterService;
import com.softwarelab.dataextractor.core.services.usecases.LibraryService;
import com.softwarelab.dataextractor.core.utilities.DateTimeUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    ProjectRepository projectRepository;
    CommiterRepository commiterRepository;
    CommitRepository commitRepository;
    ClassFileRepository classFileRepository;
    LibraryRepository libraryRepository;
    CommitLibraryRepository commitLibraryRepository;
    CommiterService commiterService;
    LibraryService libraryService;
    ClassFileService classFileService;



    @Override
    public void saveCommits(CommitModel commitModel, String fileUrl, Long projectId) {

        ProjectEntity projectEntity = projectRepository.findById(projectId).orElseThrow(()->new RuntimeException("Project not found"));

        ClassFileEntity classFileEntity = classFileRepository.findByFullPathAndProject(fileUrl,projectEntity).orElseThrow(()->new RuntimeException("Java file not found"));

        CommiterEntity commiterEntity = commiterRepository.findByEmailAndProject(commitModel.getCommiterEmail(),projectEntity).orElse(null);
        if(commiterEntity == null){
            commiterEntity = CommiterEntity.builder()
                    .name(commitModel.getCommiterName())
                    .email(commitModel.getCommiterEmail())
                    .mappedName(commitModel.getCommiterEmail())//maybe changed after classification
                    .project(projectEntity)
                    .build();
            commiterEntity = commiterRepository.save(commiterEntity);
        }

        CommitEntity commitEntity = commitRepository.findByCommitId(commitModel.getCommitId()).orElse(null);
        if(commitEntity == null){
            commitEntity = CommitEntity.builder()
                    .commitId(commitModel.getCommitId())
                    .commiter(commiterEntity)
                    .classFile(classFileEntity)
                    .authorName(commitModel.getAuthorName())
                    .authorEmail(commitModel.getAuthorEmail())
                    .authorDate(!commitModel.getAuthorDate().isBlank()?DateTimeUtil.getInstantTime(commitModel.getAuthorDate()):null)
                    .commitDate(!commitModel.getCommitDate().isBlank()?DateTimeUtil.getInstantTime(commitModel.getCommitDate()):null)
                    .build();
            commitEntity = commitRepository.save(commitEntity);
        }

        for(String lib: commitModel.getLibraries()){
            LibraryEntity libraryEntity = libraryRepository.findByName(lib).orElse(null);
            if(libraryEntity != null){
                //ensure this committer has not stored this library before
                CommitLibraryEntity commitLibraryEntity = commitLibraryRepository.findByLibraryAndCommit_Commiter(libraryEntity,commiterEntity).orElse(null);
                if(commitLibraryEntity == null){
                     commitLibraryEntity = CommitLibraryEntity.builder()
                            .commit(commitEntity)
                            .library(libraryEntity)
                             .committerLibraryCount(1)
                            .build();
                }else{
                    commitLibraryEntity.setCommitterLibraryCount(commitLibraryEntity.getCommitterLibraryCount()+1);
                }
                commitLibraryRepository.save(commitLibraryEntity);
            }
        }


    }

    @Override
    public CommitObject getCommit(Long id) {
        CommitEntity commitEntity = commitRepository.findById(id).orElseThrow(()->new RuntimeException("Commit not found"));
        return this.convertEntityToModel(commitEntity);
    }

    @Override
    public CommitObject getCommit(String commitId) {
        CommitEntity commitEntity = commitRepository.findByCommitId(commitId).orElseThrow(()->new RuntimeException("Commit not found"));
        return this.convertEntityToModel(commitEntity);
    }

    @Override
    public List<CommitObject> getFileCommits(Long classFileId) {
        ClassFileEntity classFileEntity = classFileRepository.findById(classFileId).orElseThrow(()->new RuntimeException("Java class file not found"));
        List<CommitEntity> commitEntityList = commitRepository.findAllByClassFile(classFileEntity);

        return commitEntityList.stream().map(this::convertEntityToModel).collect(Collectors.toList());
    }

    @Override
    public PagedData<CommitObject> getProjectCommits(Long projectId, int page, int size) {
        ProjectEntity projectEntity = projectRepository.findById(projectId).orElseThrow(()->new RuntimeException("Project not found"));

        Pageable pageable = PageRequest.of(page,size);
        Page<CommitEntity> commitEntityPage = commitRepository.findAllByClassFile_Project(projectEntity,pageable);
        return new PagedData<>(commitEntityPage.getContent().stream().map(this::convertEntityToModel).collect(Collectors.toList()),
                commitEntityPage.getTotalElements(),
                commitEntityPage.getTotalPages());
    }

    @Override
    public PagedData<CommitObject> getAllProjectCommits(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        Page<CommitEntity> commitEntityPage = commitRepository.findAll(pageable);
        return new PagedData<>(commitEntityPage.getContent().stream().map(this::convertEntityToModel).collect(Collectors.toList()),
                commitEntityPage.getTotalElements(),
                commitEntityPage.getTotalPages());
    }

    private CommitObject convertEntityToModel(CommitEntity commitEntity){

        return CommitObject.builder()
                .commitId(commitEntity.getCommitId())
                .commitDate(DateTimeUtil.isoDateTime(commitEntity.getCommitDate()))
                .authorDate(DateTimeUtil.isoDateTime(commitEntity.getAuthorDate()))
                .authorEmail(commitEntity.getAuthorEmail())
                .authorName(commitEntity.getAuthorName())
                .classFile(classFileService.getClassFile(commitEntity.getClassFile().getId()))
                .commiter(commiterService.getCommiter(commitEntity.getCommiter().getId()))
                .libraries(libraryService.getCommitLibraries(commitEntity.getId()))
                .build();
    }




}
