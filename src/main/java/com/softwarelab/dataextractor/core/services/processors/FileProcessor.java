package com.softwarelab.dataextractor.core.services.processors;

import com.softwarelab.dataextractor.core.exception.CMDProcessException;
import com.softwarelab.dataextractor.core.persistence.models.dtos.FileModel1;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Wilson
 * on Wed, 21/04/2021.
 */
@Service
public class FileProcessor{
    private CMDProcessor cmdProcessor;

    public FileProcessor(CMDProcessor cmdProcessor) {
        this.cmdProcessor = cmdProcessor;
    }

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
    public List<String> extractAllJavaFiles(@NonNull String projectPath) throws CMDProcessException, IOException, InterruptedException {

        List<String> lines = cmdProcessor.processCMD(CMD.ALL_GIT_MANAGED_FILES.getCommand(), projectPath);

        return lines.stream().filter(line -> line.endsWith(".java"))
                .collect(Collectors.toList());
    }

    public FileModel1 extractFileLibraries(String filePath, String projectPath) throws IOException {
        List<String> libraries = new ArrayList<>();

        List<String> lines = Files.readAllLines(Paths.get(projectPath+"\\"+filePath));
        int indexOf;
        Map<String,Boolean> libs = new HashMap<>();

        FileModel1 fileModel1 = FileModel1.builder()
                .nameUrl(filePath)
                .build();

        for(String line: lines){

            if(line.contains("class "))
                break;

            if(line.contains("package ")){
                fileModel1.setPackageName(extractPackageName(line));
                continue;
            }

            if(line.contains("import ")){
               line = line.replace("import ","").trim();
               indexOf = line.indexOf(";");
               if(indexOf >=0 )
                    libs.put(line.substring(0, indexOf),true);
            }
        }
        fileModel1.setLibraries(libs);
        return fileModel1;
    }

    private String extractPackageName(String packageName){
        packageName = packageName.replace("package ","").trim();
        int indexOf = packageName.indexOf(";");
        if(indexOf >= 0)
            packageName = packageName.substring(0,indexOf);

        return packageName;
    }


}
