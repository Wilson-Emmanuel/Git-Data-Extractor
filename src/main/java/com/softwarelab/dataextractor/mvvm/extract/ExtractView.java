package com.softwarelab.dataextractor.mvvm.extract;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Wilson
 * on Mon, 16/08/2021.
 */
@Component
public class ExtractView implements Initializable {
    @FXML
    private Button browseBtn;
    @FXML
    private TextField projectUrlOrPathTxt;

    @FXML
    private Button extractBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TextArea progressMessage;

    @Autowired
    private ExtractViewModel extractViewModel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        progressMessage.setEditable(false);


        extractViewModel.urlOrPathProperty().bind(projectUrlOrPathTxt.textProperty());

        browseBtn.disableProperty().bind(extractViewModel.taskRunningProperty());
        extractBtn.disableProperty().bind(extractViewModel.taskRunningProperty());
        cancelBtn.disableProperty().bind(extractViewModel.taskRunningProperty().not());

        extractViewModel.taskMessageProperty().addListener((observableValue, s, t1) -> {
            progressMessage.appendText(t1);
            progressMessage.setScrollLeft(0.0);
        });
        progressBar.progressProperty().bind(extractViewModel.taskProgressProperty());
        progressBar.visibleProperty().bind(extractViewModel.taskRunningProperty());

        extractBtn.setOnAction(actionEvent -> {
            progressMessage.clear();
            extractViewModel.startTask();
        });

        cancelBtn.setOnAction(actionEvent -> extractViewModel.cancelTask());
    }
}
