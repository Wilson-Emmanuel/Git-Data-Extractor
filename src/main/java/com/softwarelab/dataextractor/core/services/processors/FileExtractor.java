package com.softwarelab.dataextractor.core.services.processors;

import com.softwarelab.dataextractor.core.persistence.models.FileCountModel;
import com.softwarelab.dataextractor.core.persistence.models.requests.FileRequest;
import com.softwarelab.dataextractor.core.services.FilePackageService;
import com.softwarelab.dataextractor.core.services.FileService;
import com.softwarelab.dataextractor.core.services.ProjectService;
import com.softwarelab.dataextractor.core.exception.CMDProcessException;
import com.softwarelab.dataextractor.ui.tasks.TaskProcessor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wilson
 * on Wed, 21/04/2021.
 */
@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FileExtractor{
    CMDProcessor cmdProcessor;
    ProjectService projectService;
    FilePackageService filePackageService;
    FileService fileService;

    /**
     * This method locally extracts all external libraries used in the various .java files managed by git on the provided project.
     * return lines are file full paths starting form the folder where .git is installed
     * NB: Both .java and non java files are returned by the CMD processor. Only .java files are processed and stored in the database
     *
     * SAMPLES LINES
     * UI/org.eclipse.birt.report.debug.ui/pom.xml
     * UI/org.eclipse.birt.report.debug.ui/src/org/eclipse/birt/report/debug/internal/script/model/IScriptConstants.java
     *
     * @param projectPath valid path to the directory that contains the .git folder
     * @return FileCountModel - this contains the number of files and libraries saved
     * @throws CMDProcessException
     * @throws IOException
     */
    public FileCountModel extractAllFiles(@NonNull String projectPath, TaskProcessor taskProcessor) throws CMDProcessException, IOException, InterruptedException {

        BufferedReader bufferedReader = cmdProcessor.processCMD(CMD.ALL_GIT_MANAGED_FILES.getCommand(), projectPath);
        String line;
        //Stream<String> lines = bufferedReader.lines();
//        long lineCount = lines.count();
//        long currentCount = 0;

        List<FileRequest> fileRequests = new ArrayList<>();
        List<String> libraries;
        FileRequest fileRequest;

        while (true) {
            line = bufferedReader.readLine();
            if (line == null)
                break;

            line = line.trim();
            if (line.isBlank() || !line.endsWith(".java"))
                continue;

            libraries = extractFileLibraries(line, projectPath);
            if(!libraries.isEmpty()){
                fileRequest = FileRequest.builder()
                .libraries(libraries)
                .nameUrl(line)
                .projectPath(projectPath)
                .build();
                fileRequests.add(fileRequest);
            }
        }
        return fileService.saveBatch(fileRequests);
    }

    private List<String> extractFileLibraries(String filePath, String projectPath) throws IOException {
        List<String> libraries = new ArrayList<>();

        Path path = Paths.get(projectPath+"\\"+filePath);
        BufferedReader bufferedReader = Files.newBufferedReader(path);
        String line = bufferedReader.readLine();
        while(true){
            if(line == null){
                break;
            }
            if(line.contains(" class "))break;

            if(line.contains("package ")){
                savePackage(line,projectPath);
            }else if(line.contains("import ")){
               line = line.replace("import ","").trim();
               libraries.add(line.substring(line.indexOf(";")));
            }
            line = bufferedReader.readLine();
        }
        return libraries;
    }

    private void savePackage(String packageName, String project){
        packageName = packageName.replace("package ","").trim();
        packageName = packageName.substring(0,packageName.indexOf(";"));

        filePackageService.save(packageName,project);
    }


}
