package com.softwarelab.dataextractor.core.processors;

import com.softwarelab.dataextractor.core.persistence.models.ProjectModel;
import com.softwarelab.dataextractor.core.persistence.models.requests.ProjectRequest;
import com.softwarelab.dataextractor.core.persistence.services.usecases.ProjectUseCase;
import com.softwarelab.dataextractor.core.exception.CMDProcessException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DownloadProject {
    CMDProcessor cmdProcessor;
    ProjectUseCase projectUseCase;

    public Optional<ProjectModel> downloadProject(String basePath, String remoteGit) throws CMDProcessException, IOException {
        if(!remoteGit.endsWith(".git"))
            throw new CMDProcessException("invalid remote project url");

        //download
        cmdProcessor.execute(CMD.DOWNLOAD_PROJECT.getCommand(),basePath);

        //check if project successfully downloaded
        String projectName = remoteGit.substring(remoteGit.indexOf("/")-1,remoteGit.indexOf("."));
        String projectPath = projectName+File.pathSeparator+projectName;

        if(cmdProcessor.isValidDir(projectPath)){
            //save project
            ProjectRequest projectRequest = ProjectRequest.builder()
                    .localPath(projectPath)
                    .remoteUrl(remoteGit)
                    .name(projectName)
                    .build();
            return Optional.of(projectUseCase.save(projectRequest));
        }
        return Optional.empty();
    }

}
