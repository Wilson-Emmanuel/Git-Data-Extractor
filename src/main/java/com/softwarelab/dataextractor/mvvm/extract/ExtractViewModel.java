package com.softwarelab.dataextractor.mvvm.extract;

import com.softwarelab.dataextractor.core.exception.CMDProcessException;
import com.softwarelab.dataextractor.core.persistence.models.ProjectObject;
import com.softwarelab.dataextractor.core.persistence.models.dtos.CommitModel;
import com.softwarelab.dataextractor.core.persistence.models.dtos.FileModel;
import com.softwarelab.dataextractor.core.services.processors.FileCommitProcessor;
import com.softwarelab.dataextractor.core.services.processors.FileProcessor;
import com.softwarelab.dataextractor.core.services.processors.ProjectDownloader;
import com.softwarelab.dataextractor.core.services.usecases.CommitService;
import com.softwarelab.dataextractor.core.services.usecases.ProjectService;
import com.softwarelab.dataextractor.core.utilities.GeneralUtil;
import com.softwarelab.dataextractor.mvvm.errors.ErrorViewModel;
import com.softwarelab.dataextractor.mvvm.models.ProjectModel;
import javafx.beans.property.*;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Wilson
 * on Mon, 16/08/2021.
 */
@Component
public class ExtractViewModel {

    @Autowired
    private ErrorViewModel errorViewModel;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectModel projectModel;
    @Autowired
    private CommitService commitService;
    @Autowired
    private ProjectDownloader projectDownloader;
    @Autowired
    private FileProcessor fileProcessor;
    @Autowired
    private FileCommitProcessor fileCommitProcessor;

    private StringProperty urlOrPath = new SimpleStringProperty("");

    private Service<Void> extractionProcess = new Service<Void>() {
        @Override
        protected Task<Void> createTask() {

            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    updateMessage("Initiating...");

                    /////////////////////////////////////////////////////////
                    //1. DOWNLOADING PROJECT
                    updateMessage("Downloading project...");
                    ProjectObject projectObject = initialProcessing();

                    ///////////////////////////////////////////////////
                    //2. EXTRACT ALL FILES AND IMPORT
                    updateMessage("Extracting All Java Files...");

                    //List of Java local path in the project
                    List<String> files = fileProcessor.extractAllJavaFiles(projectObject.getLocalPath());

                    if(files.size() < 1){
                        throw new RuntimeException("Project does not have any Java files");
                    }

                    //All project packages
                    Map<String,Boolean> packg = new HashMap<>();

                    //List of all files imports
                    //A filemodel contains all imports from a file
                    List<FileModel> fileImports = new ArrayList<>();

                    int totalFiles = files.size();
                    FileModel fileModel;
                    for(int i=0; i<totalFiles; i++){
                        String filePath = files.get(i);
                        updateMessage("Extracting imports from "+filePath);
                        updateProgress(i+1,totalFiles);
                        try{
                            fileModel = fileProcessor.extractFileLibraries(filePath,projectObject.getLocalPath());
                        }catch (IOException ex){
                            continue;//ignore problematic files
                        }
                        packg.put(fileModel.getPackageName(),true);
                        fileImports.add(fileModel);
                    }

                    /////////////////////////////////////////////////////////
                    //SAVING EXTRACTED IMPORTS
                    updateMessage("Saving all project extracted imports and associated commit details...");
                    totalFiles = fileImports.size();

                    List<CommitModel> commitModels;
                    Set<String> savedFileLibraries;
                    for (int i = 0; i < totalFiles; i++) {
                        fileModel = fileImports.get(i);
                        updateProgress(i+1,totalFiles);
                        updateMessage("Processing "+fileModel.getNameUrl());

                        savedFileLibraries = projectService.saveFiles(packg,fileModel,projectObject);


                        //extract and save commits for the current fileModel
                        commitModels = fileCommitProcessor.extractFileCommits(fileModel.getNameUrl(), projectObject.getLocalPath(), savedFileLibraries);

                        for(CommitModel commitModel: commitModels)
                            commitService.saveCommits(commitModel,fileModel.getNameUrl(), projectObject.getId());
                    }

                    projectModel.updateProjects();
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    updateMessage("Done");
                }

                @Override
                protected void cancelled() {
                    super.cancelled();
                    updateMessage("Cancelled!");

                }

                @Override
                protected void failed() {
                    super.failed();
                    Throwable throwable = getException();
                    throwable.printStackTrace();
                    errorViewModel.setError(throwable.getMessage());
                    updateMessage("Failed: "+throwable.getMessage());
                }
            };
        }


    };

    private ProjectObject initialProcessing() throws CMDProcessException, IOException, InterruptedException {
        //validate urlpath
        String urlPath = urlOrPath.getValue();
        ProjectObject projectObject = null;

        //check if it's remote url and download project
        if(projectService.isValidRemoteURL(urlPath)){
            projectObject = projectDownloader.downloadAndSaveProject(GeneralUtil.getProgramPath(),urlOrPath.getValue());
        }

        //then it's localPath
        if(projectService.isLocalPathValid(urlPath)){
            projectObject = saveLocalProject(urlPath);
        }

        if(projectObject == null)
            throw new CMDProcessException("Invalid remote or local path.");

        return projectObject;
    }

    /**
     * Extract and Save ProjectObject from already downloaded project and was selected instead of
     * remote url.
     * @param urlPath
     * @return
     */
    private ProjectObject saveLocalProject(String urlPath) {
        Optional<ProjectObject> optionalProjectObject = projectService.getByLocalPath(urlPath);
        if(optionalProjectObject.isEmpty()){
            //insert project into the db
            File file = new File(urlPath);
            String projectName = file.getName();
            ProjectObject projectObject = ProjectObject.builder()
                    .localPath(urlPath)
                    .name(projectName)
                    .build();
            return projectService.saveProject(projectObject);
        }
        return optionalProjectObject.get();
    }

    public StringProperty urlOrPathProperty(){
        return urlOrPath;
    }

    public ReadOnlyStringProperty taskMessageProperty(){
        return extractionProcess.messageProperty();
    }
    public ReadOnlyBooleanProperty taskRunningProperty(){
        return extractionProcess.runningProperty();
    }
    public ReadOnlyDoubleProperty taskProgressProperty(){
        return extractionProcess.progressProperty();
    }

    public void startTask(){
        extractionProcess.restart();
    }
    public void cancelTask(){
        if(extractionProcess.isRunning())
            extractionProcess.cancel();
    }



}
