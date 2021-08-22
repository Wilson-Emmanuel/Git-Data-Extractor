package com.softwarelab.dataextractor.mvvm.export;

import com.softwarelab.dataextractor.core.persistence.models.ProjectObject;
import com.softwarelab.dataextractor.core.utilities.converters.CustomStringConverters;
import com.softwarelab.dataextractor.core.utilities.factories.ComboFactories;
import com.softwarelab.dataextractor.mvvm.models.OptionEnum;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Wilson
 * on Mon, 16/08/2021.
 */
@Component
public class ExportView implements Initializable {

    @FXML
    private ComboBox<ProjectObject> projectCmb;
    @FXML
    private ComboBox<String> optionCmb;
    @FXML
    private Button exportBtn;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressMessage;

    @Autowired
    private ExportViewModel exportViewModel;
    @Autowired
    private ComboFactories comboFactories;
    @Autowired
    private CustomStringConverters customStringConverters;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Data bindings
        exportViewModel.projectProperty().bind(projectCmb.valueProperty());
        exportViewModel.optionProperty().bind(optionCmb.valueProperty());

        progressMessage.textProperty().bind(exportViewModel.taskMessageProperty());
        progressBar.progressProperty().bind(exportViewModel.taskProgressProperty());
        exportBtn.disableProperty().bind(exportViewModel.taskRunningProperty());
        progressBar.visibleProperty().bind(exportViewModel.taskRunningProperty());
        progressMessage.visibleProperty().bind(exportViewModel.taskRunningProperty());

        //projectCmb.itemsProperty().bind(exportViewModel.getProjects());

        //Disable project when export option is ALL_PROJECT_UNIQUE_LIBRARIES( which exports all unique libraries in the system)
        optionCmb.valueProperty().addListener((observableValue, s, t1) -> {
            projectCmb.setDisable(OptionEnum.getOptionEnum(optionCmb.getSelectionModel().getSelectedItem()) == OptionEnum.ALL_PROJECT_UNIQUE_LIBRARIES);
        });

        //set export button action
        exportBtn.setOnAction(actionEvent -> {
            exportViewModel.startProcess();
        });

        //set data sources for combo boxes
        optionCmb.getItems().addAll(OptionEnum.getExportOptions());
        projectCmb.itemsProperty().bind(exportViewModel.projectsProperty());

        //Set Project combox factory and string converter
        projectCmb.setConverter(customStringConverters.getProjectObjectStringConverter());
        projectCmb.setCellFactory(comboFactories.getProjectComboCellFactory());
    }
}
