package com.softwarelab.dataextractor.ui.view_controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
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
    @FXML
    private Button clearMessageBtn;

    private ProgressBar progressBar;

    TaskProcessor taskProcessor;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //TextArea for displaying progress messages, hence non-editable
        progressMessage.setEditable(false);
        cancelBtn.setDisable(true);

        progressBar = new ProgressBar(0.0);
        progressBar.setMinWidth(progressMessage.getPrefWidth());
        progressPane.getChildren().addAll(progressBar);


        clearMessageBtn.setOnAction( actionEvent -> {
            progressMessage.clear();
        });

        extractBtn.setOnAction(actionEvent -> setupAndRunTask());

        cancelBtn.setOnAction(actionEvent -> {
            if(taskProcessor.isRunning()){
                taskProcessor.cancel(true);
            }
        });
    }
    private void setupAndRunTask(){
        taskProcessor = (TaskProcessor)applicationContext.getBean("taskProcessor") ;
        taskProcessor.setRemoteUrl(remoteUrlTxt.getText(), getProgramPath());

        taskProcessor.messageProperty().addListener((observableValue,oldValue,newValue) -> {
            progressMessage.appendText("\n"+newValue);
            progressMessage.setScrollLeft(0);
        });
        clearMessageBtn.disableProperty().bind(progressMessage.textProperty().isEmpty());
        progressBar.progressProperty().unbind();
       progressBar.progressProperty().bind(taskProcessor.progressProperty());

        extractBtn.disableProperty().bind(taskProcessor.runningProperty());
        cancelBtn.disableProperty().bind(taskProcessor.runningProperty().not());

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

}
