package com.softwarelab.dataextractor.core.services.processors;

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
     *  * This method processes all the commits made on a file. Result of the of CMD command is constructed in such a way that the commits are ordered from the oldest
     *      * to the latest commit made on a file. The first line of a every commit inforation is in the format below
     *      *
     *      *  gjdea_firstinfo: commit_id
     *      *  [followed by patches i.e. changes made on the file]
     *      *
     *      *  This method loops through this patches for every commit on a particular file and get all new libraries introduced in a particular commit, this way,
     *      *  the Developer involved can be identified.
     *      *
     *      * savedFileLibraries
     *      *  projectPath valid path to the directory that contains the .git folder
     * @param fileUrl
     * @param projectPath
     * @param savedFileLibraries
     * @return
     * @throws IOException
     * @throws CMDProcessException
     * @throws InterruptedException
     */
    public List<CommitModel> extractFileCommits(String fileUrl, String projectPath, Set<String> savedFileLibraries) throws IOException, CMDProcessException, InterruptedException {
        //return if the class file contains no import statement
        //if(savedFileLibraries.isEmpty())return Collections.emptyList();

        //all commit on a file sorted by date in ascending order (i.e. oldest commit first)
        List<String> lines = cmdProcessor.processCMD(CMD.ALL_CHANGES_MADE_ON_A_FILE.getCommand()+ fileUrl, projectPath);

        //Craete Trie from the saveLibraries
        Node root = createTrie(savedFileLibraries);

        //Extract all commits in this file and the changes made.
        //changes are temporary stored in a Hashmap using commit ID as keys
        String commitInfo = null;
        StringBuilder patch = new StringBuilder();
        List<CommitModel> commitModels = new ArrayList<>();

        for(String line: lines){

            if(line.startsWith("gjdea_firstinfo:")){//first line of every patch on the cmd query result
                if(commitInfo != null){
                    commitModels.add(getCommitModel(commitInfo, patch.toString(), fileUrl,root));
                    patch = new StringBuilder();
                }
                commitInfo = line.trim();
            }else{
                patch.append(line);
            }
        }
        //insert the last uninserted commit
        if(commitInfo != null)
            commitModels.add(getCommitModel(commitInfo, patch.toString(), fileUrl,root));

        return commitModels;
    }

    private CommitModel getCommitModel(String commitInfo, String patch,  String fileUrl, Node trieRoot){
        CommitModel commitModel = getModel(commitInfo);
        commitModel.setFileUrl(fileUrl);
        commitModel.setLibraries(getLibrariesImproved(patch, trieRoot));
        return commitModel;
    }

    private Set<String> getLibraries(String patch,  Set<String> savedLibraries) {

        Set<String> libraries = new HashSet<>();
        if(patch.isBlank() && savedLibraries.isEmpty())return libraries;

        //since patches are fetched chronologically starting from oldest, any commit patch that has any library,
        // then the library was added in the commit for the first time
        Iterator<String> iterator = savedLibraries.iterator();
        while(iterator.hasNext()) {
            String next = iterator.next();
            //if lib appears in the patch, then it was created by the author of this commit
            if(patch.contains(next)){
                libraries.add(next);
                iterator.remove();
            }
        }
        return libraries;
    }



    private CommitModel getModel(String line) {
        String[] commitInfo = line.replaceFirst("gjdea_firstinfo:", "").split("\\*\\*\\*\\*");
        //%cn **** %ce **** %an **** %ae **** %H **** %cI **** %aI
        //commiterName, commiterEmail, authorName, authorEmail, Hash, commiterDate, authorDate
        return CommitModel.builder()
                .commiterName(!commitInfo[0].isBlank() ? commitInfo[0].trim() : null)
                .commiterEmail(!commitInfo[1].isBlank() ? commitInfo[1].trim() : null)
                .authorName(!commitInfo[2].isBlank() ? commitInfo[2].trim() : null)
                .authorEmail(!commitInfo[3].isBlank() ? commitInfo[3].trim() : null)
                .commitId(!commitInfo[4].isBlank() ? commitInfo[4].trim() : null)
                .commitDate(!commitInfo[5].isBlank() ? commitInfo[5].trim() : null)
                .authorDate(!commitInfo[6].isBlank() ? commitInfo[6].trim() : null)
                .build();
    }

    private Set<String> getLibrariesImproved(String patch, Node trieRoot){
        Set<String> libraries = new HashSet<>();

        if(trieRoot.count<= 0)return libraries;

        int index = patch.indexOf("import ");
        while(index >= 0){
            //remove "import" and first trailing spaces
            index += 6;
            while(patch.charAt(index) == ' ')
                index++;

            //match strings from this point to the trie
            Node cur = trieRoot;
            for(; index < patch.length(); index++){
                char c = patch.charAt(index);
                cur = cur.children.getOrDefault(c,null);
                if(cur == null)
                    break;
                if(cur.lib != null){
                    libraries.add(cur.lib);
                    cur.lib = null;
                    trieRoot.count--;
                }
            }
            index = patch.indexOf("import ",index);
        }

        return libraries;
    }
    private Node createTrie(Set<String> libs){
        Node root = new Node();
        for(String lib: libs){
            Node cur = root;
            for(char c: lib.toCharArray()){
                if(!cur.children.containsKey(c))
                    cur.children.put(c,new Node());
                cur = cur.children.get(c);
            }
            cur.lib = lib;
            root.count = libs.size();
        }
        return root;
    }

    static class Node{
        Map<Character,Node> children = new HashMap<>();
        String lib;
        int count;
    }

}
