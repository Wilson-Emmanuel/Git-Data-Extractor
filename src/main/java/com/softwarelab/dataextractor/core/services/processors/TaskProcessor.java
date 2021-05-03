package com.softwarelab.dataextractor.core.services.processors;

import com.softwarelab.dataextractor.core.exception.CMDProcessException;
import com.softwarelab.dataextractor.core.persistence.models.ProjectModel;
import com.softwarelab.dataextractor.core.services.processors.CommitExtractor;
import com.softwarelab.dataextractor.core.services.processors.FileCommitLibraryExtractor;
import com.softwarelab.dataextractor.core.services.processors.FileExtractor;
import com.softwarelab.dataextractor.core.services.processors.ProjectDownloader;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
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
/*
  @Override
    public void start(Stage stage) throws Exception {
        StackPane pane = new StackPane();
        Scene scene = new Scene(pane,600,700);

        stage.setScene(scene);
        stage.setTitle("JavaFX Example");
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
 */
    @Override
    protected Void call() throws Exception {

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
        this.updateMessage(String.format("%s \n%s",ex.getMessage(),"Failed!"));
        updateProgress(0,0);
    }
}
