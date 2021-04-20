package com.softwarelab.dataextractor.core.processors;

import com.softwarelab.dataextractor.core.domain.models.requests.CommitRequest;
import com.softwarelab.dataextractor.core.domain.services.CommitService;
import com.softwarelab.dataextractor.core.domain.services.ProjectService;
import com.softwarelab.dataextractor.core.exception.CMDProcessException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wilson
 * on Mon, 19/04/2021.
 */
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommitExtractor {

    ProjectService projectService;
    CommitService commitService;

    /**
     * This method locally extracts all commits and save their information in the DB.
     * This git command is constructed in such a way that the following 2 sample lines are generated for each commit
     * Notice how **** are used to delimit the commit details
     * SAMPLES LINES
     * commit a6b2bc1a4eeb2c28577f78c606de8a47f940fe1c
     * firstinfo: author_name: Wim Jongman **** author_email: wim.jongman@remainsoftware.com **** commit_hash: a6b2bc1a4eeb2c28577f78c606de8a47f940fe1c **** author_date: 2021-04-15 20:10:58 +0200
     *
     * @param projectPath - valid path to the directory that contains the .git folder
     * @return - number of commits saved
     * @throws CMDProcessException
     * @throws IOException
     */
    public int extractAllCommits(@NonNull String projectPath) throws CMDProcessException, IOException {
        if (!projectService.existsByLocalPath(projectPath))
            throw new CMDProcessException("Invalid Project Location");

        if (!CMDProcessor.isValidDir(projectPath))
            throw new CMDProcessException("Invalid git directory. Directory must contain the .git folder");

        BufferedReader bufferedReader = CMDProcessor.processCMD(CMD.ALL_LOCAL_COMMITS_IN_ALL_BRANCHES.getCommand(), projectPath);
        String line;
        List<CommitRequest> commitRequests = new ArrayList<>();
        while (true) {
            line = bufferedReader.readLine();
            if (line == null)
                break;
            if (line.startsWith("commit"))
                continue;
            commitRequests.add(extractCommit(line, projectPath));
        }
        return commitService.batchSave(commitRequests,projectPath);
    }

    private CommitRequest extractCommit(String line, String projectPath) {
        String[] commitInfo = line.replaceFirst("firstinfo:", "").split("\\*\\*\\*\\*");

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
