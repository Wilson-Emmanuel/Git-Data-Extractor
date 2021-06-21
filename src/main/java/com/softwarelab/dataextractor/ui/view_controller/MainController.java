package com.softwarelab.dataextractor.ui.view_controller;

import com.softwarelab.dataextractor.core.utilities.GeneralUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component(value = "mainController")
@Scope("prototype")
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

    @FXML
    private Button exportBtn;

    @Value("classpath:/export.fxml")
    private Resource exportResource;

    private ProgressBar progressBar;

    ExtractionTask extractionTask;

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

        exportBtn.setOnAction(this::openExportWindow);

        extractBtn.setOnAction(actionEvent -> setupAndRunTask());

        cancelBtn.setOnAction(actionEvent -> {
            if(extractionTask.isRunning()){
                extractionTask.cancel(true);
            }
        });
    }
    @SneakyThrows
    private void openExportWindow(ActionEvent actionEvent) {
        if(extractionTask != null && extractionTask.isRunning()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Process Termination");
            alert.setContentText("Do you want to terminate the current process?");
            ButtonType buttonType = alert.showAndWait().orElse(ButtonType.NO);
            if(buttonType == ButtonType.OK && extractionTask != null && extractionTask.isRunning()){
                extractionTask.cancel(true);
            }
        }

        FXMLLoader fxmlLoader = new FXMLLoader(exportResource.getURL());
        ExportController exportController = (ExportController) applicationContext.getBean("exportController");
        fxmlLoader.setController(exportController);

        Parent parent = fxmlLoader.load();
        parent.getStyleClass().add("mainbg");

        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

        Scene scene = new Scene(parent);
        scene.getStylesheets().add("/main.css");
        stage.setScene(scene);

    }
    private void setupAndRunTask(){
        extractionTask = (ExtractionTask)applicationContext.getBean("taskProcessor") ;
        extractionTask.setRemoteUrl(remoteUrlTxt.getText(), GeneralUtil.getProgramPath());

        extractionTask.messageProperty().addListener((observableValue, oldValue, newValue) -> {
            progressMessage.appendText("\n"+newValue);
            //progressMessage.setScrollLeft(Double.MIN_VALUE);
        });
        progressMessage.textProperty().addListener((ob,od,nw)->progressMessage.setScrollLeft(0));

        clearMessageBtn.disableProperty().bind(progressMessage.textProperty().isEmpty());
        progressBar.progressProperty().unbind();
       progressBar.progressProperty().bind(extractionTask.progressProperty());

        extractBtn.disableProperty().bind(extractionTask.runningProperty());
        cancelBtn.disableProperty().bind(extractionTask.runningProperty().not());

        Thread taskThread = new Thread(extractionTask);
        taskThread.start();
    }
   


}
