package com.softwarelab.dataextractor.ui.view_controller;

import com.softwarelab.dataextractor.core.exception.CMDProcessException;
import com.softwarelab.dataextractor.core.persistence.models.dtos.FileModel;
import com.softwarelab.dataextractor.core.persistence.models.dtos.ProjectModel;
import com.softwarelab.dataextractor.core.services.usecases.CommitService;
import com.softwarelab.dataextractor.ui.processors.FileCommitProcessor;
import com.softwarelab.dataextractor.ui.processors.FileProcessor;
import com.softwarelab.dataextractor.ui.processors.ProjectDownloader;
import javafx.concurrent.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * Created by Wilson
 * on Tue, 25/05/2021.
 */
@Service(value = "taskProcessor")
@Scope("prototype")
public class TaskProcessor extends Task<Void> {
    private String remoteUrl;
    private String basePath;

    @Autowired
    private ProjectDownloader projectDownloader;
    @Autowired
    private FileProcessor fileProcessor;
    @Autowired
    private FileCommitProcessor fileCommitProcessor;
    @Autowired
    private CommitService commitService;


    @Override
    protected Void call() throws Exception {
        if (remoteUrl.isBlank() || !remoteUrl.trim().endsWith(".git"))
            throw new CMDProcessException("Invalid remote project URL.");

        //downloading projects
        ProjectModel project = null;
        if (!this.isCancelled()) {
            updateMessage("Downloading project from "+remoteUrl+"...");
            project = projectDownloader.downloadProject(basePath, remoteUrl);
            if (project == null)
                throw new CMDProcessException("Project download unsuccessful.");

            updateMessage("Project successfully downloaded at: " + project.getLocalPath());
            updateProgress(0.0,0.0);
        }
        //extracting files and libs
        Objects.requireNonNull(project);
        List<FileModel> fileModels = new ArrayList<>();
        Set<String> packages = new HashSet<>();

        if(!this.isCancelled()){
            updateMessage("Extracting all Java files from the project.");
            List<String> files = fileProcessor.extractAllJavaFiles(project.getLocalPath());
            updateProgress(1.0,1.0);
            updateProgress(0,0);
            updateMessage("Extracting all Libraries");
            FileModel fileModel;
            for(int i=0; i<files.size(); i++){
                String fileName = files.get(i);
                updateProgress(i+1,files.size());
                updateMessage("Extracting libraries from "+fileName);

                fileModel = fileProcessor.extractFileLibraries(fileName,project.getLocalPath());
                if(!fileModel.getLibraries().isEmpty()){
                    packages.add(fileModel.getPackageName());
                    fileModels.add(fileModel);
                }
            }
            files.clear();
        }

        //extracting file commits
        //List<CommitModel> commitModels = new ArrayList<>();
        if(!this.isCancelled()){
            updateMessage("Analyzing all commits from Java files.");
            updateProgress(0,0);
            for(int i=0; i<fileModels.size(); i++){
                String fileName = fileModels.get(i).getNameUrl();
                updateProgress(i+1,fileModels.size());
                updateMessage("Analyzing commit patches from "+fileName);
                commitService.saveCommitAll(fileCommitProcessor.extractFileCommits(fileModels.get(i),project.getLocalPath(),packages));
            }
        }


        updateMessage("Removing local project files");
        File localProjectFolder = new File(project.getLocalPath());
        if(localProjectFolder.exists()){
            deleteDir(localProjectFolder);
        }
        return null;
    }
    public static void deleteDir(File dirFile) {
        if (dirFile.isDirectory()) {
            File[] dirs = dirFile.listFiles();
            if(dirs == null)return;
            for (File dir: dirs) {
                deleteDir(dir);
            }
        }
        dirFile.delete();
    }
    public void setRemoteUrl(String url, String basePath){
        this.remoteUrl = url;
        this.basePath = basePath;
    }
    @Override
    protected void succeeded() {
        super.succeeded();
        this.updateMessage("Done!");
        updateProgress(100,100);
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        this.updateMessage("Cancelled!");
        updateProgress(0,0);
    }

    @Override
    protected void failed() {
        super.failed();
        Throwable ex = this.getException();
        ex.printStackTrace();
        this.updateMessage(String.format("Process Incomplete: %s \n",ex.getMessage()));
        updateProgress(0,0);
    }
}
