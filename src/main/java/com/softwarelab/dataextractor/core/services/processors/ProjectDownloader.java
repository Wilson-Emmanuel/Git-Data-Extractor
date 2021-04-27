package com.softwarelab.dataextractor.core.services.processors;

import com.softwarelab.dataextractor.core.persistence.models.ProjectModel;
import com.softwarelab.dataextractor.core.persistence.models.requests.ProjectRequest;
import com.softwarelab.dataextractor.core.services.usecases.ProjectUseCase;
import com.softwarelab.dataextractor.core.exception.CMDProcessException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProjectDownloader {
    CMDProcessor cmdProcessor;
    ProjectUseCase projectUseCase;

    public Optional<ProjectModel> downloadProject(String basePath, String remoteGit) throws CMDProcessException, IOException, InterruptedException {
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
        ProjectRequest projectRequest = ProjectRequest.builder()
                .localPath(projectPath)
                .remoteUrl(remoteGit)
                .name(projectName)
                .build();
        return Optional.of(projectUseCase.save(projectRequest));
    }

}
