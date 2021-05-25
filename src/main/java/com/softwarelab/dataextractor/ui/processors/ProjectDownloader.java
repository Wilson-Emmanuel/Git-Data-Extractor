package com.softwarelab.dataextractor.ui.processors;

import com.softwarelab.dataextractor.core.exception.CMDProcessException;
import com.softwarelab.dataextractor.core.persistence.models.dtos.ProjectModel;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ProjectDownloader {
    private CMDProcessor cmdProcessor;



    public ProjectDownloader(CMDProcessor cmdProcessor) {
        this.cmdProcessor = cmdProcessor;
    }

    public ProjectModel downloadProject(String basePath, String remoteGit) throws CMDProcessException, IOException, InterruptedException {
        if(!remoteGit.endsWith(".git"))
            throw new CMDProcessException("invalid remote project url");

        //check if project successfully downloaded
        String projectName = remoteGit.substring(remoteGit.lastIndexOf("/")+1,remoteGit.lastIndexOf("."));
        String projectPath = basePath+"\\"+projectName;

        //download project
        cmdProcessor.execute(CMD.DOWNLOAD_PROJECT.getCommand()+remoteGit,basePath);

        //check if it was successfully downloaded
        if(!cmdProcessor.isValidDir(projectPath))
            throw new CMDProcessException("Invalid project path: "+projectPath);

        //save and return project if successful
        return ProjectModel.builder()
                .localPath(projectPath)
                .remoteUrl(remoteGit)
                .name(projectName)
                .build();
    }

}
