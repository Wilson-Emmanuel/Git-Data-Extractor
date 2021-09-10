package com.softwarelab.dataextractor.core.services;

import com.softwarelab.dataextractor.core.persistence.entities.CommitEntity;
import com.softwarelab.dataextractor.core.persistence.entities.CommitLibraryEntity;
import com.softwarelab.dataextractor.core.persistence.entities.LibraryEntity;
import com.softwarelab.dataextractor.core.persistence.entities.ProjectEntity;
import com.softwarelab.dataextractor.core.persistence.models.LibraryObject;
import com.softwarelab.dataextractor.core.persistence.models.PagedData;
import com.softwarelab.dataextractor.core.persistence.repositories.*;
import com.softwarelab.dataextractor.core.services.usecases.LibraryService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Wilson
 * on Thu, 02/09/2021.
 */
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LibraryServiceImpl implements LibraryService {

    LibraryRepository libraryRepository;
    CommitLibraryRepository commitLibraryRepository;
    CommitRepository commitRepository;
    ClassLibraryRepository classLibraryRepository;
    ProjectRepository projectRepository;

    @Override
    public Set<LibraryObject> getCommitLibraries(Long commitId) {
        CommitEntity commitEntity = commitRepository.findById(commitId).orElseThrow(()->new RuntimeException("commit not found"));
        List<CommitLibraryEntity> libraryEntities = commitLibraryRepository.findAllByCommit(commitEntity);
        return libraryEntities.stream()
                .map(CommitLibraryEntity::getLibrary)
                .map(libraryEntity -> convertLibraryEntityToObject(libraryEntity,commitEntity))
                .collect(Collectors.toSet());
    }

    @Override
    public PagedData<LibraryObject> getAllUniqueLibraries(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);

        //all libraries in the library table are already unique
        Page<LibraryEntity> libraryEntityPage = libraryRepository.findAll(pageable);

        return new PagedData<>(libraryEntityPage.getContent().stream().map(this::convertLibraryEntityToObject).collect(Collectors.toList()),
                libraryEntityPage.getTotalElements(),
                libraryEntityPage.getTotalPages());
    }

    @Override
    public PagedData<LibraryObject> getUnclassifiedLibraries(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        Page<LibraryEntity> libraryEntityPage = libraryRepository.getUnclassifiedLibs(pageable);

        return new PagedData<>(libraryEntityPage.getContent().stream().map(this::convertLibraryEntityToObject).collect(Collectors.toList()),
                libraryEntityPage.getTotalElements(),
                libraryEntityPage.getTotalPages());
    }

    @Override
    public void updateLibrary(String name, String provider, String category) {
        LibraryEntity libraryEntity = libraryRepository.findByName(name).orElse(null);
        if(libraryEntity == null){
            libraryEntity = LibraryEntity.builder()
                    .name(name)
                    .provider(provider)
                    .category(category)
                    .build();
        }else{
            libraryEntity.setCategory(category);
            libraryEntity.setProvider(provider);
        }
        libraryRepository.save(libraryEntity);
    }

    @Override
    public Set<LibraryObject> getUniqueProjectLibraries(Long projectId) {
        ProjectEntity projectEntity = projectRepository.findById(projectId).orElseThrow(()->new RuntimeException("Project not found"));
        List<LibraryEntity> libraryObjects = classLibraryRepository.getUniqueProjectLibraries(projectEntity);

        return libraryObjects.stream().map(this::convertLibraryEntityToObject).collect(Collectors.toSet());
    }
    @Override
    public Set<LibraryObject> getUnclassifiedUniqueProjectLibraries(Long projectId) {
        ProjectEntity projectEntity = projectRepository.findById(projectId).orElseThrow(()->new RuntimeException("Project not found"));
        List<LibraryEntity> libraryObjects = classLibraryRepository.getUnclassifiedUniqueProjectLibraries(projectEntity);

        return libraryObjects.stream().map(this::convertLibraryEntityToObject).collect(Collectors.toSet());
    }

    private LibraryObject convertLibraryEntityToObject(LibraryEntity entity){
        return LibraryObject.builder()
                .id(entity.getId())
                .name(entity.getName())
                .category(entity.getCategory())
                .provider(entity.getProvider())
                .build();
    }
    private LibraryObject convertLibraryEntityToObject(LibraryEntity entity, CommitEntity commitEntity){
        return LibraryObject.builder()
                .id(entity.getId())
                .name(entity.getName())
                .category(entity.getCategory())
                .provider(entity.getProvider())
                .libraryFrequencyInProject(commitLibraryRepository.getLibraryFrequencyInProject(entity,commitEntity))
                .build();
    }
}
