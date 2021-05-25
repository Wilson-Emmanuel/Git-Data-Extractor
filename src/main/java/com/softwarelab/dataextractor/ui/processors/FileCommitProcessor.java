package com.softwarelab.dataextractor.ui.processors;

import com.softwarelab.dataextractor.core.exception.CMDProcessException;
import com.softwarelab.dataextractor.core.persistence.models.dtos.CommitModel;
import com.softwarelab.dataextractor.core.persistence.models.dtos.FileModel;
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
public class FileCommitProcessor {

    CMDProcessor cmdProcessor;


    /**
     * This method processes all the commits made on a file. Result of the of CMD command is constructed in such a way that the commits are ordered from the oldest
     * to the latest commit made on a file. The first line of a every commit inforation is in the format below
     *
     *  gjdea_firstinfo: commit_id
     *  [followed by patches i.e. changes made on the file]
     *
     *  This method loops through this patches for every commit on a particular file and get all new libraries introduced in a particular commit, this way,
     *  the Developer involved can be identified.
     *
     * @param fileModel
     * @param projectPath valid path to the directory that contains the .git folder
     * @param packages
     * @return
     * @throws IOException
     * @throws CMDProcessException
     * @throws InterruptedException
     */
    public List<CommitModel> extractFileCommits(FileModel fileModel, String projectPath, Set<String> packages) throws IOException, CMDProcessException, InterruptedException {
        //all commit on a file sorted by date ascending (i.e. oldest commit first)
        List<String> lines = cmdProcessor.processCMD(CMD.ALL_CHANGES_MADE_ON_A_FILE.getCommand()+fileModel.getNameUrl(), projectPath);

        //Extract all commits in this file and the changes made.
        //changes are temporary stored in a Hashmap using commit ID as keys
        String commitInfo = null;
        StringBuilder sb = new StringBuilder();
        List<CommitModel> commitModels = new ArrayList<>();

        for(String line: lines){
            if(fileModel.getLibraries().isEmpty())break;

            if(line.startsWith("gjdea_firstinfo:")){//first line of every patch on the cmd query result
                if(commitInfo != null){
                    commitModels.add(getCommitModel(commitInfo,sb.toString(),packages,fileModel));
                    sb = new StringBuilder();
                }
                commitInfo = line.trim();
            }else{
                sb.append(line);
            }
        }
        //insert the last uninserted commit
        if(commitInfo != null && !fileModel.getLibraries().isEmpty())
            commitModels.add(getCommitModel(commitInfo,sb.toString(),packages,fileModel));

        return commitModels;
    }
    private CommitModel getCommitModel(String commitInfo, String patch, Set<String> packages, FileModel fileModel){
        CommitModel commitModel = getModel(commitInfo);
        commitModel.setFileUrl(fileModel.getNameUrl());
        commitModel.setLibraries(getLibraries(patch, packages, fileModel));
        return commitModel;
    }

    private Set<String> getLibraries(String patch, Set<String> packages, FileModel fileModel) {
        Set<String> libraries = new HashSet<>();
        Iterator<String> iterator = fileModel.getLibraries().iterator();

        //since patches are fetched chronologically starting from oldest, any commit patch that has any library,
        // then the library was added in the commit for the first time
        while(iterator.hasNext()) {
            String curLib = iterator.next();

            //remove lib if project package
            if(packages.contains(curLib.substring(0,curLib.lastIndexOf(".")))){
                iterator.remove();
                continue;
            }

            //if lib appears in the patch, then it was created by the author of this commit
            if(patch.contains(curLib)){
                libraries.add(curLib);
                iterator.remove();
            }
        }
        return libraries;
    }

    private CommitModel getModel(String line) {
        String[] commitInfo = line.replaceFirst("gjdea_firstinfo:", "").split("\\*\\*\\*\\*");

        //name,email,hash,date
        return CommitModel.builder()
                .developerName(commitInfo[0].trim())
                .developerEmail(!commitInfo[1].isBlank() ? commitInfo[1].trim() : null)
                .commitId(commitInfo[2].trim())
                .commitDate(commitInfo[3].trim())
                .build();
    }


}
