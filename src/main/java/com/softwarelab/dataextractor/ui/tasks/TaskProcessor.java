package com.softwarelab.dataextractor.ui.tasks;

import com.softwarelab.dataextractor.core.exception.CMDProcessException;
import com.softwarelab.dataextractor.core.persistence.models.ProjectModel;
import com.softwarelab.dataextractor.core.services.processors.CommitExtractor;
import com.softwarelab.dataextractor.core.services.processors.FileCommitLibraryExtractor;
import com.softwarelab.dataextractor.core.services.processors.FileExtractor;
import com.softwarelab.dataextractor.core.services.processors.ProjectDownloader;
import javafx.concurrent.Task;
import javafx.scene.control.Button;

import java.util.Optional;

public class TaskProcessor extends Task<Void> {
    private String remoteUrl;
    private String basePath;
    private Button extractBtn;
    private Button cancelBtn;

    private ProjectDownloader projectDownloader;
    private FileExtractor fileExtractor;
    private CommitExtractor commitExtractor;
    private FileCommitLibraryExtractor fileCommitLibraryExtractor;

    public TaskProcessor( Button btn, Button cancelBtn){
        this.extractBtn = btn;
        this.cancelBtn = cancelBtn;
    }

    public void setProjectUrlAndPath(String remoteUrl, String basePath) {
        this.remoteUrl = remoteUrl;
        this.basePath = basePath;
    }

    @Override
    protected Void call() throws Exception {

        extractBtn.setDisable(true);
        cancelBtn.setDisable(false);

        if (remoteUrl.isBlank() || !remoteUrl.trim().endsWith(".git"))
            throw new CMDProcessException("Invalid remote project url.");


        if (!this.isCancelled()) {
            updateMessage("Downloading project...");
            Optional<ProjectModel>  optionalProjectModel = projectDownloader.downloadProject(basePath, remoteUrl);
            if (optionalProjectModel.isEmpty())
                throw new CMDProcessException("Project download unsuccessful.");

            updateMessage("Project successfully downloaded at: " + optionalProjectModel.get().getLocalPath());
            updateProgress(0.0,0.0);


        }
        return null;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        completeTask("Done!");
        updateProgress(100,100);

    }

    @Override
    protected void cancelled() {
        super.cancelled();
        completeTask("Cancelled!");

    }

    @Override
    protected void failed() {
        super.failed();
        Throwable ex = this.getException();
        completeTask(String.format("%s \n%s",ex.getMessage(),"Failed!"));

    }
    private void completeTask(String message){
        this.updateMessage(message);
        extractBtn.setDisable(false);
        cancelBtn.setDisable(true);
    }

    public void setProjectDownloader(ProjectDownloader projectDownloader) {
        this.projectDownloader = projectDownloader;
    }

    public void setFileExtractor(FileExtractor fileExtractor) {
        this.fileExtractor = fileExtractor;
    }

    public void setCommitExtractor(CommitExtractor commitExtractor) {
        this.commitExtractor = commitExtractor;
    }

    public void setFileCommitLibraryExtractor(FileCommitLibraryExtractor fileCommitLibraryExtractor) {
        this.fileCommitLibraryExtractor = fileCommitLibraryExtractor;
    }

}
