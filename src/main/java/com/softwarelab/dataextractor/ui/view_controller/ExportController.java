package com.softwarelab.dataextractor.ui.view_controller;

import com.softwarelab.dataextractor.core.persistence.models.dtos.ProjectModel;
import com.softwarelab.dataextractor.core.services.usecases.ProjectService;
import com.softwarelab.dataextractor.core.utilities.GeneralUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.StringConverter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Wilson
 * on Tue, 08/06/2021.
 */
@Component(value = "exportController")
@Scope("prototype")//all maintained controllers should be prototyped
public class ExportController implements Initializable {

    @FXML
    private ComboBox<ProjectModel> projectCmb;

    @FXML
    private ComboBox<String> exportOptionCmb;

    @FXML
    private Button exportBtn;
    @FXML
    private Button extractBtn;

    @Autowired
    private ApplicationContext applicationContext;

    @Value("classpath:/main.fxml")
    private Resource extractResource;

    @Autowired
    private ProjectService projectService;

    private  ObservableList<ProjectModel> projects = FXCollections.emptyObservableList();
    private ExportLibraryTask exportLibraryTask;

    private String defaultBtnName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        projects = FXCollections.observableArrayList(projectService.getAllProjects());
        defaultBtnName = exportBtn.getText();

        projectCmb.setPromptText("Select Project");
        projectCmb.getItems().setAll(projects);
        setUpComboFactory();

        exportOptionCmb.setPromptText("Select Option");
        exportOptionCmb.getItems().addAll(ExportOption.BY_DEVELOPER.toString(), ExportOption.BY_LIBRARY.toString());

        extractBtn.setOnAction(this::openExtractWindow);

        exportBtn.setOnAction(this::exportByLibrary);

    }


    private void exportByLibrary(ActionEvent actionEvent){
        if(projectCmb.getSelectionModel().getSelectedItem() == null || exportOptionCmb.getSelectionModel().getSelectedItem() == null){
            throw new RuntimeException("Select project or Export Option");
        }

        exportLibraryTask = (ExportLibraryTask) applicationContext.getBean("exportLibrary");
        exportLibraryTask.setProject(projectCmb.getSelectionModel().getSelectedItem());
        exportLibraryTask.setByDev(ExportOption.BY_DEVELOPER.toString().equals(exportOptionCmb.getSelectionModel().getSelectedItem()));
        String exportLocation = getExportLocation(actionEvent);
        if(exportLocation.isBlank()){
            //throw  new RuntimeException("Select export location");
            return;
        }
        exportLibraryTask.setExportLocation(exportLocation);

        exportBtn.disableProperty().bind(exportLibraryTask.runningProperty());

        exportBtn.textProperty().bind(exportLibraryTask.messageProperty());
        exportLibraryTask.setOnCancelled(event-> resetButton());
        exportLibraryTask.setOnFailed(event-> resetButton());
        exportLibraryTask.setOnSucceeded(event-> resetButton());

        Thread thread = new Thread(exportLibraryTask);
        thread.start();
    }
    private void resetButton(){
        exportBtn.textProperty().unbind();
        exportBtn.setText(defaultBtnName);
    }


    private String getExportLocation(ActionEvent actionEvent){
        Window window = ((Node)actionEvent.getSource()).getScene().getWindow();
        DirectoryChooser directoryChooser  = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(GeneralUtil.getProgramPath()));
        directoryChooser.setTitle("Export Location");
        File directoryLocation = directoryChooser.showDialog(window);
        if(directoryLocation == null)return null;
        return directoryLocation.getAbsolutePath();
    }
    private void setUpComboFactory() {

        projectCmb.setConverter(new StringConverter<ProjectModel>() {
            private Map<String,ProjectModel> map = new HashMap<>();
            @Override
            public String toString(ProjectModel model) {
                if(model != null){
                    map.put(model.getName(),model);
                    return model.getName();
                }
                return "";
            }

            @Override
            public ProjectModel fromString(String s) {
                if(!s.isBlank()){
                    return map.get(s);
                }
                return null;
            }
        });


        projectCmb.setCellFactory(new Callback<>() {
            @Override
            public ListCell<ProjectModel> call(ListView<ProjectModel> projectModelListView) {

                return new ListCell<ProjectModel>() {
                    @Override
                    protected void updateItem(ProjectModel model, boolean empty) {
                        super.updateItem(model, empty);
                        if (model == null || empty) {
                            setText(" ");
                        } else {
                            setText(model.getName());
                        }
                    }
                };
            }
        });
    }


    @SneakyThrows
    private void openExtractWindow(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(extractResource.getURL());
        MainController extractController = (MainController) applicationContext.getBean("mainController");
        fxmlLoader.setController(extractController);

        Parent parent = fxmlLoader.load();
        parent.getStyleClass().add("mainbg");

        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

        Scene scene = new Scene(parent);
        scene.getStylesheets().add("/main.css");
        stage.setScene(scene);

    }

    enum ExportOption{
        BY_LIBRARY("By Library"),
        BY_DEVELOPER("By_Developer");

        private String option;
        ExportOption(String o){
            this.option = o;
        }
        @Override
        public String toString(){
            return option;
        }
    }
}
