package com.softwarelab.dataextractor.core.persistence.repositories;

import com.softwarelab.dataextractor.core.persistence.entities.CommitEntity;
import com.softwarelab.dataextractor.core.persistence.entities.CommitLibraryEntity;
import com.softwarelab.dataextractor.core.persistence.entities.CommiterEntity;
import com.softwarelab.dataextractor.core.persistence.entities.LibraryEntity;
import com.softwarelab.dataextractor.core.persistence.enums.RecordStatusConstant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by Wilson
 * on Mon, 16/08/2021.
 */
@Repository
public interface CommitLibraryRepository extends JpaRepository<CommitLibraryEntity,Long> {


    Optional<CommitLibraryEntity> findByCommitAndLibrary(CommitEntity commit, LibraryEntity library);

    boolean existsByLibraryAndCommit_Commiter(LibraryEntity library, CommiterEntity committer);
    Optional<CommitLibraryEntity> findByLibraryAndCommit_Commiter(LibraryEntity library, CommiterEntity committer);
    List<CommitLibraryEntity> findAllByCommit(CommitEntity commit);

    List<CommitLibraryEntity> findAllByCommit_Commiter(CommiterEntity committer);

    @Query("select c.committerLibraryCount from CommitLibraryEntity c where c.library = :library and c.commit = :commit")
    int getLibraryFrequencyInProject(@Param("library") LibraryEntity library, @Param("commit") CommitEntity commit);

    void deleteAllByRecordStatus(RecordStatusConstant status);
}
