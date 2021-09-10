package com.softwarelab.dataextractor.core.services;

import com.softwarelab.dataextractor.core.persistence.entities.CommitLibraryEntity;
import com.softwarelab.dataextractor.core.persistence.entities.CommiterEntity;
import com.softwarelab.dataextractor.core.persistence.entities.ProjectEntity;
import com.softwarelab.dataextractor.core.persistence.enums.RecordStatusConstant;
import com.softwarelab.dataextractor.core.persistence.models.CommiterObject;
import com.softwarelab.dataextractor.core.persistence.models.PagedData;
import com.softwarelab.dataextractor.core.persistence.repositories.CommitLibraryRepository;
import com.softwarelab.dataextractor.core.persistence.repositories.CommiterRepository;
import com.softwarelab.dataextractor.core.persistence.repositories.ProjectRepository;
import com.softwarelab.dataextractor.core.services.usecases.CommiterService;
import com.softwarelab.dataextractor.core.services.usecases.ProjectService;
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
 * on Thu, 02/09/2021.
 */
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommiterServiceImpl implements CommiterService {

    ProjectService projectService;
    ProjectRepository projectRepository;
    CommiterRepository commiterRepository;

    CommitLibraryRepository commitLibraryRepository;

    @Override
    public CommiterObject getCommiter(Long commiterId) {
        CommiterEntity commiterEntity = commiterRepository.findById(commiterId).orElseThrow(()->new RuntimeException("Commiter not found"));
        return this.convertCommiterEntityToObject(commiterEntity);
    }

    @Override
    public List<CommiterObject> getProjectCommiters(Long projectId) {
        ProjectEntity projectEntity = projectRepository.findById(projectId).orElseThrow(()->new RuntimeException("Project not found"));

        List<CommiterEntity> commiterEntities = commiterRepository.findAllByProject(projectEntity);

        return commiterEntities.stream().map(this::convertCommiterEntityToObject).collect(Collectors.toList());
    }

    @Override
    public PagedData<CommiterObject> getAllCommiters(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        Page<CommiterEntity> commiterEntities = commiterRepository.findAll(pageable);

        return new PagedData<>(commiterEntities.getContent().stream().map(this::convertCommiterEntityToObject).collect(Collectors.toList()),
                commiterEntities.getTotalElements(),
                commiterEntities.getTotalPages());
    }



    private CommiterObject convertCommiterEntityToObject(CommiterEntity entity){
        return CommiterObject.builder()
                .email(entity.getEmail())
                .name(entity.getName())
                .mappedName(entity.getMappedName())
                //.project(projectService.getProject(entity.getProject().getId()))
                .build();
    }
}
