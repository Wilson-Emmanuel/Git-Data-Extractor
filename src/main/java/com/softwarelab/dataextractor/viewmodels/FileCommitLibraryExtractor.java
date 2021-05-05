package com.softwarelab.dataextractor.viewmodels;

import com.softwarelab.dataextractor.core.exception.CMDProcessException;
import com.softwarelab.dataextractor.core.persistence.models.FileModel;
import com.softwarelab.dataextractor.core.persistence.models.PagedData;
import com.softwarelab.dataextractor.core.persistence.models.requests.CommitAndContentRequest;
import com.softwarelab.dataextractor.core.persistence.models.requests.CommitRequest;
import com.softwarelab.dataextractor.core.services.FileService;
import com.softwarelab.dataextractor.core.services.usecases.CommitAndContentUseCase;
import com.softwarelab.dataextractor.core.services.usecases.CommitUseCase;
import com.softwarelab.dataextractor.core.services.usecases.FileCommitUseCase;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Created by Wilson
 * on Wed, 21/04/2021.
 */
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileCommitLibraryExtractor extends ProgressUpdator{

    CMDProcessor cmdProcessor;
    FileCommitUseCase fileCommitUseCase;
    CommitUseCase commitUseCase;
    CommitExtractor commitExtractor;
    CommitAndContentUseCase commitAndContentUseCase;
    FileService fileService;

    public void linkLibsToCommits(String projectPath) throws InterruptedException, CMDProcessException, IOException {
        message.set("Linking libraries to commits...");

        //fetch all files page by page
        int curPage = 0, size = 50;
        PagedData<FileModel> allFiles = fileService.getProjectFiles(projectPath,curPage++,size);
        total.set(allFiles.getTotalItems());
        int pages = allFiles.getTotalPages();
        int runningTotl = 1;

        do{
            for(FileModel fileModel:allFiles.getItems()){
                runningTotal.set(runningTotl++);
                linkCommitsToLibraries(fileModel,projectPath);
            }
            allFiles = fileService.getProjectFiles(projectPath,curPage++,size);
        }while (curPage < pages);

    }

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
    private void linkCommitsToLibraries(FileModel model, String projectPath) throws IOException, CMDProcessException, InterruptedException {
        //all commit on a file sorted by date ascending (i.e. oldest commit first)
        List<String> lines = cmdProcessor.processCMD(CMD.ALL_CHANGES_MADE_ON_A_FILE.getCommand()+model.getNameUrl(), projectPath);

        //Extract all commits in this file and the changes made.
        //changes are temporary stored in a Hashmap using commit ID as keys
        String commitInfo = null;
        StringBuilder sb = new StringBuilder();
        Map<CommitRequest, String> commitPatches = new HashMap<>();

        for(String line: lines){
            if(line.startsWith("gjdea_firstinfo:")){//first line of every patch on the cmd query result
                if(commitInfo != null){
                    commitPatches.put(commitExtractor.extractCommit(commitInfo,projectPath),sb.toString());
                    sb = new StringBuilder();
                }
                commitInfo = line.trim();
            }else{
                sb.append(line);
            }
        }
        //insert the last uninserted commit
        if(commitInfo != null)
            commitPatches.put(commitExtractor.extractCommit(commitInfo,projectPath),sb.toString());

        updateDB(commitPatches,model);
    }

    private void updateDB(Map<CommitRequest,String> commitPatches, FileModel fileModel){

        Set<String> fileLibraries = fileModel.getLibraries();
        List<CommitAndContentRequest> curCommitLibs;
            for(Map.Entry<CommitRequest,String> patch : commitPatches.entrySet()){

                //ensure commit exists in the DB or save it
                if(!commitUseCase.existsByCommitId(patch.getKey().getCommitId()))
                    commitUseCase.save(patch.getKey());

                //save commit and file
                fileCommitUseCase.save(fileModel.getId(), patch.getKey().getCommitId());

                //extract libraries in this commit
                curCommitLibs = new ArrayList<>();//all libs added in the current patch for the first time
                Iterator<String> iterator = fileLibraries.iterator();

                //since patches are fetched chronologically starting from oldest, any commit patch that has any library,
                // then the library was added in the commit for the first time
                while(iterator.hasNext()){
                    String curLib = iterator.next();
                    if(patch.getValue().contains(curLib)){
                        curCommitLibs.add(CommitAndContentRequest.builder()
                                        .commitId(patch.getKey().getCommitId())
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
