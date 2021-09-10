package com.softwarelab.dataextractor.mvvm.imports;

import com.softwarelab.dataextractor.core.persistence.models.ProjectObject;
import com.softwarelab.dataextractor.core.services.usecases.ImportService;
import com.softwarelab.dataextractor.mvvm.errors.ErrorViewModel;
import com.softwarelab.dataextractor.mvvm.event_aggregator.EventHub;
import com.softwarelab.dataextractor.mvvm.models.OptionEnum;
import com.softwarelab.dataextractor.mvvm.models.ProjectModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Wilson
 * on Thu, 19/08/2021.
 */
@Component
public class ImportViewModel {

    private ErrorViewModel errorViewModel;
    private ProjectModel projectModel;
    private EventHub eventHub;
    private ImportService importService;

    private ObjectProperty<ObservableList<ProjectObject>> projectsProperty = new SimpleObjectProperty<>(FXCollections.emptyObservableList());


    private StringProperty urlPath = new SimpleStringProperty("");
    private StringProperty optionProperty = new SimpleStringProperty("");
    private ObjectProperty<ProjectObject> projectProperty = new SimpleObjectProperty<>(null);

    private Service<Void> importProcess = new Service<Void>() {
        @Override
        protected Task<Void> createTask() {
            return new Task<>() {
                @Override
                protected Void call() {
                    try{
                        OptionEnum optionEnum = OptionEnum.getOptionEnum(optionProperty.getValue());
                        if(optionEnum == null){
                            errorViewModel.setError("No option is selected");
                            return null;
                        }

                        switch (optionEnum){
                            case IMPORT_LIBRARIES:
                                importLibraries();
                                break;
                            default:
                                errorViewModel.setError("Invalid import option selected");
                        }
                    }catch (Exception ex){
                        errorViewModel.setError(ex.getMessage());
                    }
                    return null;
                }

                private void importLibraries() throws Exception {
                    String fileName = urlPath.getValue();
                    if(fileName.isBlank() || !fileName.endsWith(".xlsx")){
                        throw  new Exception("Only .xlsx excel file is allowed!");
                    }

                    updateMessage("Updating classified libraries...");
                    FileInputStream file = new FileInputStream(fileName);

                    //Create Workbook instance holding reference to .xlsx file
                    XSSFWorkbook workbook = new XSSFWorkbook(file);
                    importService.importClassifiedLibrary(workbook);

                    file.close();
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    updateMessage("Done");
                }

                @Override
                protected void cancelled() {
                    super.cancelled();
                    updateMessage("Cancelled!");

                }

                @Override
                protected void failed() {
                    super.failed();
                    Throwable throwable = getException();
                    errorViewModel.setError(throwable.getMessage());
                    updateMessage("Failed: "+throwable.getMessage());
                }
            };
        }
    };
    public ImportViewModel(ErrorViewModel errorViewModel, ProjectModel projectModel, EventHub eventHub, ImportService importService){
        this.errorViewModel = errorViewModel;
        this.projectModel = projectModel;
        this.eventHub = eventHub;
        this.importService = importService;
        this.eventHub.subscribe(EventHub.EVENT_PROJECT_UPDATE,this,this::updateProjectEvent);
        updateProjectEvent("");
    }

    public StringProperty optionProperty(){
        return optionProperty;
    }
    public ObjectProperty<ProjectObject> projectProperty(){
        return projectProperty;
    }
    public ReadOnlyStringProperty taskMessageProperty(){
        return importProcess.messageProperty();
    }
    public ReadOnlyBooleanProperty taskRunningProperty(){
        return importProcess.runningProperty();
    }
    public ReadOnlyDoubleProperty taskProgressProperty(){
        return importProcess.progressProperty();
    }
    public StringProperty urlPathProperty(){
        return urlPath;
    }
    public void startProcess(){
        importProcess.restart();
    }
    public ObjectProperty<ObservableList<ProjectObject>> projectsProperty(){
        return projectsProperty;
    }

    /**
     * triggered by the event aggregator
     * @param event
     */
    private void updateProjectEvent(String event){
        projectsProperty.setValue(FXCollections.observableArrayList(projectModel.getProjects()));
    }
}
