package com.softwarelab.dataextractor.ui.controllers;

import com.softwarelab.dataextractor.core.services.processors.*;
import com.softwarelab.dataextractor.core.services.processors.TaskProcessor;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class MainController implements Initializable {
    @FXML
    private TextField remoteUrlTxt;
    @FXML
    private Button extractBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private StackPane progressPane;
    @FXML
    private TextArea progressMessage;

    private Text progressIndicator;
    private ProgressBar progressBar;

    @Autowired
    private ProjectDownloader projectDownloader;

    @Autowired
    private FileExtractor fileExtractor;

    @Autowired
    private CommitExtractor commitExtractor;

    @Autowired
    private FileCommitLibraryExtractor fileCommitLibraryExtractor;
    TaskProcessor taskProcessor;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //TextArea for displaying progress messages, hence non-editable
        progressMessage.setEditable(false);
        cancelBtn.setDisable(true);

        progressIndicator = new Text();
        progressBar = new ProgressBar(0.0);
        progressBar.setMinWidth(progressMessage.getPrefWidth());
        progressPane.getChildren().addAll(progressBar,progressIndicator);


        extractBtn.setOnAction(actionEvent -> {
            setupAndRunTask();
        });

        cancelBtn.setOnAction(actionEvent -> {
            if(taskProcessor.isRunning()){
                taskProcessor.cancel(true);
            }
        });

    }
    private void setupAndRunTask(){
        taskProcessor = (TaskProcessor)applicationContext.getBean("taskProcessor") ;
        taskProcessor.setProjectUrlAndPath(remoteUrlTxt.getText(), getProgramPath());

        taskProcessor.messageProperty().addListener((observableValue,oldValue,newValue) -> {
            progressMessage.appendText("\n"+newValue);
        });
        progressBar.progressProperty().unbind();
        progressBar.setProgress(0.0);
       progressBar.progressProperty().bind(taskProcessor.progressProperty());
        taskProcessor.progressProperty().addListener((observableValue,oldValue,newValue)->{
            double value = newValue.byteValue()*100.0;
            progressIndicator.setText(value+"%");
        });
        taskProcessor.setOnCancelled(workerStateEvent -> {
            cancelBtn.setDisable(true);
            extractBtn.setDisable(false);
        });
        taskProcessor.setOnFailed(workerStateEvent -> {
            cancelBtn.setDisable(true);
            extractBtn.setDisable(false);
        });
        taskProcessor.setOnScheduled(workerStateEvent -> {
            cancelBtn.setDisable(false);
            extractBtn.setDisable(true);
        });
        taskProcessor.setOnSucceeded(workerStateEvent -> {
            cancelBtn.setDisable(true);
            extractBtn.setDisable(false);
        });

        Thread taskThread = new Thread(taskProcessor);
        taskThread.start();
    }
   
    private String getProgramPath(){
        File defaultLoc  = new File(System.getProperty("user.home"),"Data_Extractor");
        boolean created = defaultLoc.exists();
        if(!created){
            created = defaultLoc.mkdir();
        }
        return created?defaultLoc.getPath():System.getProperty("user.home");
    }
    //https://github.com/apache/shiro.git
}
