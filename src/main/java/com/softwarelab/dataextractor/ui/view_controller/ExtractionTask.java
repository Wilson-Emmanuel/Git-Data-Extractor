package com.softwarelab.dataextractor.ui.view_controller;

import com.softwarelab.dataextractor.core.exception.CMDProcessException;
import com.softwarelab.dataextractor.core.persistence.entities.ProjectEntity;
import com.softwarelab.dataextractor.core.persistence.models.dtos.FileModel1;
import com.softwarelab.dataextractor.core.services.usecases.CommitService1;
import com.softwarelab.dataextractor.core.services.processors.FileCommitProcessor;
import com.softwarelab.dataextractor.core.services.processors.FileProcessor;
import com.softwarelab.dataextractor.core.services.processors.ProjectDownloader;
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
public class ExtractionTask extends Task<Void> {
    private String remoteUrl;
    private String basePath;

    @Autowired
    private ProjectDownloader projectDownloader;
    @Autowired
    private FileProcessor fileProcessor;
    @Autowired
    private FileCommitProcessor fileCommitProcessor;
    @Autowired
    private CommitService1 commitService1;


    @Override
    protected Void call() throws Exception {
        if (remoteUrl.isBlank() || !remoteUrl.trim().endsWith(".git"))
            throw new CMDProcessException("Invalid remote project URL.");

        //downloading projects
        ProjectEntity project = null;
        if (!this.isCancelled()) {
            updateMessage("Downloading project from "+remoteUrl+"...");
            //project = projectDownloader.downloadProject(basePath, remoteUrl);
            if (project == null)
                throw new CMDProcessException("Project download unsuccessful.");

            updateMessage("Project successfully downloaded at: " + project.getLocalPath());
            updateProgress(0.0,0.0);
        }
        //extracting files and libs
        Objects.requireNonNull(project);
        List<FileModel1> fileModel1s = new ArrayList<>();
        Set<String> packages = new HashSet<>();

        if(!this.isCancelled()){
            updateMessage("Extracting all Java files from the project.");
            List<String> files = fileProcessor.extractAllJavaFiles(project.getLocalPath());
            updateProgress(1.0,1.0);
            updateProgress(0,0);
            updateMessage("Extracting all Libraries");
            FileModel1 fileModel1;
            for(int i=0; i<files.size(); i++){
                String fileName = files.get(i);
                updateProgress(i+1,files.size());
                updateMessage("Extracting libraries from "+fileName);

                fileModel1 = fileProcessor.extractFileLibraries(fileName,project.getLocalPath());
                if(!fileModel1.getLibraries().isEmpty()){
                    packages.add(fileModel1.getPackageName());
                    fileModel1s.add(fileModel1);
                }
            }
            files.clear();
        }

        //extracting file commits
        //List<CommitModel1> commitModels = new ArrayList<>();
        if(!this.isCancelled()){
            updateMessage("Analyzing all commits from Java files.");
            updateProgress(0,0);
            for(int i = 0; i< fileModel1s.size(); i++){
                String fileName = fileModel1s.get(i).getNameUrl();
                updateProgress(i+1, fileModel1s.size());
                updateMessage("Analyzing commit patches from "+fileName);
                commitService1.saveCommitAll(fileCommitProcessor.extractFileCommits(fileModel1s.get(i),project.getLocalPath(),packages), project );
            }
        }


        updateMessage("Removing local project files");
        File localProjectFolder = new File(project.getLocalPath());
        if(localProjectFolder.exists()){
           // deleteDir(localProjectFolder);
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
        //ex.printStackTrace();
        this.updateMessage(String.format("Process Incomplete: %s \n",ex.getMessage()));
        updateProgress(0,0);
    }
}
