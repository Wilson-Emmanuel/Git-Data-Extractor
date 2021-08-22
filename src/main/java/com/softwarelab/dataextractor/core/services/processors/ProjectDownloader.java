package com.softwarelab.dataextractor.core.services.processors;

import com.softwarelab.dataextractor.core.exception.CMDProcessException;
import com.softwarelab.dataextractor.core.persistence.models.ProjectObject;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ProjectDownloader {
    private CMDProcessor cmdProcessor;


    public ProjectDownloader(CMDProcessor cmdProcessor) {
        this.cmdProcessor = cmdProcessor;
    }

    public ProjectObject downloadProject(String basePath, String remoteGit) throws CMDProcessException, IOException, InterruptedException {
        if(!remoteGit.endsWith(".git"))
            throw new CMDProcessException("invalid remote project url");

       //extract project path
        String projectName = remoteGit.substring(remoteGit.lastIndexOf("/")+1,remoteGit.lastIndexOf("."));
        String projectPath = basePath+"\\"+projectName;

        //download project
        cmdProcessor.execute(CMD.DOWNLOAD_PROJECT.getCommand()+remoteGit,basePath);

        //check if it was successfully downloaded
        if(!cmdProcessor.isValidDir(projectPath))
            throw new CMDProcessException("Download unsuccessful: "+projectPath);

        //return project if successful

        return ProjectObject.builder()
                .localPath(projectPath)
                .remoteURL(remoteGit)
                .name(projectName)
                .build();

    }

}
