package com.softwarelab.dataextractor.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class MainController implements Initializable {
    @FXML
    private TextField remoteUrlTxt;
    @FXML
    private Button extractBtn;
    @FXML
    private ProgressBar progress;
    @FXML
    private TextArea messageTxt;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
