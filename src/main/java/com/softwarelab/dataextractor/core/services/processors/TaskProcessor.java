package com.softwarelab.dataextractor.core.services.processors;

import com.softwarelab.dataextractor.core.exception.CMDProcessException;
import com.softwarelab.dataextractor.core.persistence.models.ProjectModel;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service(value = "taskProcessor")
@Scope("prototype")
public class TaskProcessor extends Task<Void> {
    private String remoteUrl;
    private String basePath;

    @Autowired
    private ProjectDownloader projectDownloader;
    @Autowired
    private FileExtractor fileExtractor;
    @Autowired
    private CommitExtractor commitExtractor;
    @Autowired
    private FileCommitLibraryExtractor fileCommitLibraryExtractor;


    public void setProjectUrlAndPath(String remoteUrl, String basePath) {
        this.remoteUrl = remoteUrl;
        this.basePath = basePath;
    }

    @Override
    protected Void call() throws Exception {
        SimpleDoubleProperty total = new SimpleDoubleProperty(0.0);
        ChangeListener<Number> totalChangeListener = (obv, oldV, newV)->total.set(newV.doubleValue());
        ChangeListener<String> messageChangeListener =(obv, oldV, newV)-> updateMessage(newV);
        ChangeListener<Number> runningTotalChangeListener = (obv, oldV, newV)->updateProgress(newV.doubleValue(),total.doubleValue());

        if (remoteUrl.isBlank() || !remoteUrl.trim().endsWith(".git"))
            throw new CMDProcessException("Invalid remote project url.");

        //downloading projects
        String projectPath = null;
        if (!this.isCancelled()) {
            updateMessage("Downloading project from "+remoteUrl+"...");
            Optional<ProjectModel>  optionalProjectModel = projectDownloader.downloadProject(basePath, remoteUrl);
            if (optionalProjectModel.isEmpty())
                throw new CMDProcessException("Project download unsuccessful.");

            projectPath = optionalProjectModel.get().getLocalPath();
            updateMessage("Project successfully downloaded at: " + optionalProjectModel.get().getLocalPath());
            updateProgress(0.0,0.0);
        }
        //extracting files and libs
        if(!this.isCancelled()){
            fileExtractor.bindListener(messageChangeListener,totalChangeListener,runningTotalChangeListener);
            fileExtractor.extractAllFiles(projectPath);
            fileExtractor.unbindListener(messageChangeListener,totalChangeListener,runningTotalChangeListener);
        }
        return null;
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
        this.updateMessage(String.format("%s \n%s",ex.getMessage(),"Failed!"));
        updateProgress(0,0);
    }
}
