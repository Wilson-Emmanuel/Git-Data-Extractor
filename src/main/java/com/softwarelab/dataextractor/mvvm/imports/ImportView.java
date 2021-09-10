package com.softwarelab.dataextractor.mvvm.imports;

import com.softwarelab.dataextractor.core.persistence.models.ProjectObject;
import com.softwarelab.dataextractor.core.utilities.GeneralUtil;
import com.softwarelab.dataextractor.core.utilities.converters.CustomStringConverters;
import com.softwarelab.dataextractor.core.utilities.factories.ComboFactories;
import com.softwarelab.dataextractor.mvvm.models.OptionEnum;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Wilson
 * on Mon, 16/08/2021.
 */
@Component
public class ImportView implements Initializable {
    @FXML
    private Button browseBtn;
    @FXML
    private TextField urlPathTxt;
    @FXML
    private ComboBox<ProjectObject> projectCmb;
    @FXML
    private ComboBox<String> optionCmb;
    @FXML
    private Button importBtn;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressMessage;

    @Autowired
    private ImportViewModel importViewModel;
    @Autowired
    private ComboFactories comboFactories;
    @Autowired
    private CustomStringConverters customStringConverters;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        urlPathTxt.setEditable(false);

        //Data bindings
        importViewModel.projectProperty().bind(projectCmb.valueProperty());
        importViewModel.optionProperty().bind(optionCmb.valueProperty());
        importViewModel.urlPathProperty().bind(urlPathTxt.textProperty());

        progressMessage.textProperty().bind(importViewModel.taskMessageProperty());
        progressBar.progressProperty().bind(importViewModel.taskProgressProperty());
        importBtn.disableProperty().bind(importViewModel.taskRunningProperty());
        progressBar.visibleProperty().bind(importViewModel.taskRunningProperty());
        progressMessage.visibleProperty().bind(importViewModel.taskRunningProperty());

        //Disable project when export option is IMPORT_LIBRARIES( which exports all unique libraries in the system)
        optionCmb.valueProperty().addListener((observableValue, s, t1) -> {
            projectCmb.setDisable(OptionEnum.getOptionEnum(optionCmb.getSelectionModel().getSelectedItem()).isAllProject());
        });

        //set data sources for combo boxes
        optionCmb.getItems().addAll(OptionEnum.getImportOptions());
        projectCmb.itemsProperty().bind(importViewModel.projectsProperty());

        //configure import button action event
        importBtn.setOnAction(actionEvent -> {
            importViewModel.startProcess();
        });

        //Set Project combox factory and string converter
        projectCmb.setConverter(customStringConverters.getProjectObjectStringConverter());
        projectCmb.setCellFactory(comboFactories.getProjectComboCellFactory());

        browseBtn.setOnAction(actionEvent -> urlPathTxt.setText(getFileLocation(actionEvent)));

    }

    private String getFileLocation(ActionEvent actionEvent){
        Window window = ((Node)actionEvent.getSource()).getScene().getWindow();
        FileChooser fileChooser  = new FileChooser();
        fileChooser.setInitialDirectory(new File(GeneralUtil.getProgramPath()));
        fileChooser.setTitle("Select Import File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel File","*.xlsx"));
        File directoryLocation = fileChooser.showOpenDialog(window);
        if(directoryLocation == null)return "";
        return directoryLocation.getAbsolutePath();
    }
}
