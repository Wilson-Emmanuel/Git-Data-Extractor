package com.softwarelab.dataextractor.core.processors;

import com.softwarelab.dataextractor.core.domain.models.FileModel;
import com.softwarelab.dataextractor.core.domain.models.requests.CommitAndContentRequest;
import com.softwarelab.dataextractor.core.domain.models.requests.CommitRequest;
import com.softwarelab.dataextractor.core.domain.services.usecases.CommitAndContentUseCase;
import com.softwarelab.dataextractor.core.domain.services.usecases.CommitUseCase;
import com.softwarelab.dataextractor.core.domain.services.usecases.FileCommitUseCase;
import com.softwarelab.dataextractor.core.exception.CMDProcessException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by Wilson
 * on Wed, 21/04/2021.
 */
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileCommitLibraryExtractor {

    CMDProcessor cmdProcessor;
    FileCommitUseCase fileCommitUseCase;
    CommitUseCase commitUseCase;
    CommitExtractor commitExtractor;
    CommitAndContentUseCase commitAndContentUseCase;

    /**
     * This method processes all the commits made on a file. Result of the of CMD command is constructed in such a way that the commits are ordered from the oldest
     * to the latest commit made on a file. The first line of a every commit inforation is in the format below
     *
     * gjdea_firstinfo: commit_id
     * [followed by patches i.e. changes made on the file]
     *
     * This method loops through this patches for every commit on a particular file and get all new libraries introduced in a particular commit, this way,
     * the Developer involved can be identified.
     *
     * @param model
     * @param projectPath - valid path to the directory that contains the .git folder
     * @throws IOException
     * @throws CMDProcessException
     */
    public void linkCommitsToLibraries(FileModel model, String projectPath) throws IOException, CMDProcessException {
        BufferedReader bufferedReader = cmdProcessor.processCMD(CMD.ALL_CHANGES_MADE_ON_A_FILE.getCommand()+model.getNameUrl(), projectPath);

        //Extract all commits in this file and the changes made.
        //changes are temporary stored in a Hashmap using commit ID as keys
        String line,commitId = bufferedReader.readLine().replace("gjdea_firstinfo:","").trim();
        StringBuilder sb = new StringBuilder();
        Map<CommitRequest, String> commitPatches = new HashMap<>();
        while(true){
            line = bufferedReader.readLine();

            if(line == null || line.startsWith("gjdea_firstinfo:")){
                commitPatches.put(commitExtractor.extractCommit(commitId,projectPath),sb.toString());
                commitId = line;
            }else{
                sb.append(line).append("\n");
            }
            if(line == null)
                break;
        }
        updateDB(commitPatches,model);
    }

    private void updateDB(Map<CommitRequest,String> commitPatches, FileModel fileModel){

        Set<String> fileLibraries = fileModel.getLibraries();
            for(Map.Entry<CommitRequest,String> entry: commitPatches.entrySet()){

                //ensure commit exists in the DB or save it
                if(!commitUseCase.existsByCommitId(entry.getKey().getCommitId()))
                    commitUseCase.save(entry.getKey());

                //save commit and file
                fileCommitUseCase.save(fileModel.getId(),entry.getKey().getCommitId());

                //extract libraries in this commit
                List<CommitAndContentRequest> curCommitLibs = new ArrayList<>();
                Iterator<String> iterator = fileLibraries.iterator();

                //since patches are fetched chronologically, any patch that has any library, then the library was added
                //in the commit for the firs time
                while(iterator.hasNext()){
                    String curLib = iterator.next();
                    if(entry.getValue().contains(curLib)){
                        curCommitLibs.add(CommitAndContentRequest.builder()
                                        .commitId(entry.getValue())
                                        .fileId(fileModel.getId())
                                        .library(curLib)
                                        .build());
                        iterator.remove();
                    }
                }
                //Ignore commits with no new libraries added
                if(!curCommitLibs.isEmpty())
                    commitAndContentUseCase.saveBatchPerFile(curCommitLibs);

                //if fileLibraries becomes empty, then all later commits have no new library added
                if(fileLibraries.isEmpty())
                    break;
            }
    }
}
