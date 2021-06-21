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

        //remove app packages from the file libraries
        removeAppPackages(fileModel, packages);

        //Extract all commits in this file and the changes made.
        //changes are temporary stored in a Hashmap using commit ID as keys
        String commitInfo = null;
        List<String> patch = new ArrayList<>();
        List<CommitModel> commitModels = new ArrayList<>();

        for(String line: lines){
            if(fileModel.getLibraries().isEmpty())break;//later patches/commits do not have new libraries introduced

            if(line.startsWith("gjdea_firstinfo:")){//first line of every patch on the cmd query result
                if(commitInfo != null){
                    commitModels.add(getCommitModel(commitInfo, patch,fileModel));
                    patch = new ArrayList<>();
                }
                commitInfo = line.trim();
            }else{
                patch.add(line);
            }
        }
        //insert the last uninserted commit
        if(commitInfo != null && !fileModel.getLibraries().isEmpty())
            commitModels.add(getCommitModel(commitInfo, patch,fileModel));

        return commitModels;
    }

    private void removeAppPackages(FileModel fileModel, Set<String> packages) {
        fileModel.getLibraries().keySet().removeIf(curLib -> packages.contains(curLib.substring(0, curLib.lastIndexOf("."))));
    }

    private CommitModel getCommitModel(String commitInfo, List<String> patch, FileModel fileModel){
        CommitModel commitModel = getModel(commitInfo);
        commitModel.setFileUrl(fileModel.getNameUrl());
        commitModel.setLibraries(getLibraries(patch, fileModel));
        return commitModel;
    }

    private Set<String> getLibraries(List<String> patch,  FileModel fileModel) {
        List<String> validPatchLibs  = getValidPathLibs(patch);
        Set<String> libraries = new HashSet<>();
        if(validPatchLibs.isEmpty())
            return libraries;



        //since patches are fetched chronologically starting from oldest, any commit patch that has any library,
        // then the library was added in the commit for the first time
        for(String curLib: validPatchLibs) {
            //if lib appears in the patch, then it was created by the author of this commit
            if(fileModel.getLibraries().containsKey(curLib)){
                libraries.add(curLib);
                fileModel.getLibraries().remove(curLib);
            }
        }
        return libraries;
    }

    private List<String> getValidPathLibs(List<String> patch) {
        List<String> valid = new ArrayList<>();
        for(String line: patch){
            if(line.contains("class ") || line.contains("public ") || line.contains("private ") || line.contains("protected") || line.contains("{"))
                break;
            int index = line.indexOf("import ");
            if(index >= 0){
                int last = line.indexOf(';',index+7);
                valid.add(line.substring(index+7, last>=0?last:line.length()));
            }
        }
        return valid;
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
