package com.softwarelab.dataextractor.core.services.processors;

import com.softwarelab.dataextractor.core.exception.CMDProcessException;
import com.softwarelab.dataextractor.core.persistence.models.ProjectObject;
import com.softwarelab.dataextractor.core.services.usecases.ProjectService;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ProjectDownloader {
    private CMDProcessor cmdProcessor;
    ProjectService projectService;


    public ProjectDownloader(CMDProcessor cmdProcessor, ProjectService projectService) {
        this.cmdProcessor = cmdProcessor;
        this.projectService = projectService;
    }

    public ProjectObject downloadAndSaveProject(String basePath, String remoteGit) throws CMDProcessException, IOException, InterruptedException {
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

        ProjectObject projectObject = ProjectObject.builder()
                .localPath(projectPath)
                .remoteURL(remoteGit)
                .name(projectName)
                .build();
        return projectService.saveProject(projectObject);
    }

}
