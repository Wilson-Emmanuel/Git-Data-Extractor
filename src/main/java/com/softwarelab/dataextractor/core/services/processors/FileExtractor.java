package com.softwarelab.dataextractor.core.services.processors;

import com.softwarelab.dataextractor.core.persistence.models.FileCountModel;
import com.softwarelab.dataextractor.core.persistence.models.requests.FileRequest;
import com.softwarelab.dataextractor.core.services.FilePackageService;
import com.softwarelab.dataextractor.core.services.FileService;
import com.softwarelab.dataextractor.core.services.ProjectService;
import com.softwarelab.dataextractor.core.exception.CMDProcessException;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
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
import java.util.stream.Collectors;

/**
 * Created by Wilson
 * on Wed, 21/04/2021.
 */
@Service
public class FileExtractor{
    private CMDProcessor cmdProcessor;
    private FilePackageService filePackageService;
    private FileService fileService;

    private SimpleStringProperty message = new SimpleStringProperty("");
    private SimpleDoubleProperty total = new SimpleDoubleProperty(0.0);
    private SimpleDoubleProperty runningTotal = new SimpleDoubleProperty(0.0);

    public FileExtractor(CMDProcessor cmdProcessor, FilePackageService filePackageService, FileService fileService) {
        this.cmdProcessor = cmdProcessor;
        this.filePackageService = filePackageService;
        this.fileService = fileService;
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
    public void extractAllFiles(@NonNull String projectPath) throws CMDProcessException, IOException, InterruptedException {
        message.set("Extracting files and libraries");

        List<String> lines = cmdProcessor.processCMD(CMD.ALL_GIT_MANAGED_FILES.getCommand(), projectPath);

        total.set(lines.size());
        long runningTotl = 1;

        List<FileRequest> fileRequests = new ArrayList<>();
        List<String> libraries;
        FileRequest fileRequest;

        for(String line: lines) {
            runningTotal.set(runningTotl++);

            line = line.trim();
            if (line.isBlank() || !line.endsWith(".java"))
                continue;

            message.set("Extracting libraries from "+line);
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
        //if nothing is extracted, throw exception. It could fatal error from CMD
        if(fileRequests.isEmpty())
            throw new CMDProcessException("No file and library extracted: "+(lines.size()==1?lines.get(0):""));

        message.set("Saving extracted files and libraries");
        FileCountModel fileCountModel = fileService.saveBatch(fileRequests);
        message.set("Files: "+fileCountModel.fileCount+", Libraries: "+fileCountModel.libraryCount);
    }

    private List<String> extractFileLibraries(String filePath, String projectPath) throws IOException {
        List<String> libraries = new ArrayList<>();

        List<String> lines = Files.readAllLines(Paths.get(projectPath+"\\"+filePath));
        int indexOf;
        for(String line: lines){

            if(line.contains("class "))
                break;

            if(line.contains("package ")){
                savePackage(line,projectPath);
                continue;
            }

            if(line.contains("import ")){
               line = line.replace("import ","").trim();
               indexOf = line.indexOf(";");
               if(indexOf >=0 )
                    libraries.add(line.substring(0, indexOf));
            }
        }
        return libraries;
    }

    private void savePackage(String packageName, String project){
        packageName = packageName.replace("package ","").trim();
        int indexOf = packageName.indexOf(";");
        if(indexOf >= 0)
            packageName = packageName.substring(0,indexOf);

        filePackageService.save(packageName,project);
    }

    public void bindListener(ChangeListener<String> messageListener, ChangeListener<Number> totalListener, ChangeListener<Number> runningTotalListener){
        total.addListener(totalListener);
        runningTotal.addListener(runningTotalListener);
        message.addListener(messageListener);
    }
    public void unbindListener(ChangeListener<String> messageListener, ChangeListener<Number> totalListener, ChangeListener<Number> runningTotalListener){
        total.removeListener(totalListener);
        runningTotal.removeListener(runningTotalListener);
        message.removeListener(messageListener);
    }


}
