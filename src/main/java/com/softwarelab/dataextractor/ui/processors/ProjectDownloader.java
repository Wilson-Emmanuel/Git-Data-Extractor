package com.softwarelab.dataextractor.ui.processors;

import com.softwarelab.dataextractor.core.exception.CMDProcessException;
import com.softwarelab.dataextractor.core.persistence.entities.ProjectEntity;
import com.softwarelab.dataextractor.core.persistence.models.dtos.ProjectModel;
import com.softwarelab.dataextractor.core.services.usecases.ProjectService;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ProjectDownloader {
    private CMDProcessor cmdProcessor;
    private ProjectService projectService;


    public ProjectDownloader(CMDProcessor cmdProcessor, ProjectService projectService) {
        this.cmdProcessor = cmdProcessor;
        this.projectService = projectService;
    }

    public ProjectEntity downloadProject(String basePath, String remoteGit) throws CMDProcessException, IOException, InterruptedException {
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

        ProjectModel model= ProjectModel.builder()
                .localPath(projectPath)
                .remoteUrl(remoteGit)
                .name(projectName)
                .build();
        return projectService.saveProject(model);
    }

}
