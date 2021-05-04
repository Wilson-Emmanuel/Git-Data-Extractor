package com.softwarelab.dataextractor.viewmodels;

import com.softwarelab.dataextractor.core.exception.CMDProcessException;
import com.softwarelab.dataextractor.core.persistence.models.requests.CommitRequest;
import com.softwarelab.dataextractor.core.services.CommitService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wilson
 * on Mon, 19/04/2021.
 */
@Service
public class CommitExtractor extends ProgressUpdator {
    private CommitService commitService;
    private CMDProcessor cmdProcessor;


    public CommitExtractor(CommitService commitService, CMDProcessor cmdProcessor) {
        this.commitService = commitService;
        this.cmdProcessor = cmdProcessor;
    }

    /**
     * This method locally extracts all commits and save their information in the DB.
     * This git command is constructed in such a way that the following 2 sample lines are generated for each commit
     * Notice how **** are used to delimit the commit details
     * SAMPLES LINES
     * commit a6b2bc1a4eeb2c28577f78c606de8a47f940fe1c
     * gjdea_firstinfo: Wim Jongman **** wim.jongman@remainsoftware.com **** a6b2bc1a4eeb2c28577f78c606de8a47f940fe1c **** 2021-04-15 20:10:58 +0200
     *
     * @param projectPath - valid path to the directory that contains the .git folder
     * @return - number of commits saved
     * @throws CMDProcessException
     * @throws IOException
     */
    public void extractAllCommits(@NonNull String projectPath) throws CMDProcessException, IOException, InterruptedException {
        message.set("Extracting all commits...");

        List<String> lines = cmdProcessor.processCMD(CMD.ALL_LOCAL_COMMITS_IN_ALL_BRANCHES.getCommand(), projectPath);
        List<CommitRequest> commitRequests = new ArrayList<>();

        long runningTotl = 1;
        total.set(lines.size());
        System.out.println(String.join("",lines));
        for(String line: lines) {
            runningTotal.set(runningTotl++);

            if (line.startsWith("gjdea_firstinfo:")){
                commitRequests.add(extractCommit(line, projectPath));
            }
        }
        //if nothing is extracted, throw exception. It could be a fatal error from CMD
        if(commitRequests.isEmpty())
            throw new CMDProcessException("No commit extracted: "+(lines.size()==1?lines.get(0):""));

        message.set("Setting all extracted commits...");
        int totalSaved = commitService.batchSave(commitRequests,projectPath);
        message.set(totalSaved+" commits saved!");
    }

    public CommitRequest extractCommit(String line, String projectPath) {
        String[] commitInfo = line.replaceFirst("gjdea_firstinfo:", "").split("\\*\\*\\*\\*");

        //name,email,hash,date
        return CommitRequest.builder()
                .developerName(commitInfo[0].trim())
                .developerEmail(!commitInfo[1].isBlank() ? commitInfo[1].trim() : null)
                .projectPath(projectPath)
                .commitId(commitInfo[2].trim())
                .commitDate(commitInfo[3].trim())
                .build();
    }

}
