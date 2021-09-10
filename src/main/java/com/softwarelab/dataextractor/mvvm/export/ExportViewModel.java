package com.softwarelab.dataextractor.mvvm.export;

import com.softwarelab.dataextractor.core.persistence.models.ProjectObject;
import com.softwarelab.dataextractor.core.services.usecases.CommiterService;
import com.softwarelab.dataextractor.core.services.usecases.ExportService;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Wilson
 * on Wed, 18/08/2021.
 */
@Component
public class ExportViewModel {


    private ErrorViewModel errorViewModel;
    private ProjectModel projectModel;
    private EventHub eventHub;
    private ExportService exportService;

    private ObjectProperty<ObservableList<ProjectObject>> projectsProperty = new SimpleObjectProperty<>(FXCollections.emptyObservableList());

   private StringProperty optionProperty = new SimpleStringProperty("");
   private ObjectProperty<ProjectObject> projectProperty = new SimpleObjectProperty<>(null);

   private String exportDestination = "";

    private Service<Void> exportProcess = new Service<Void>() {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {

                    OptionEnum optionEnum = OptionEnum.getOptionEnum(optionProperty.getValue());
                    if(optionEnum == null){
                        throw new Exception("No option is selected");
                    }
                    ProjectObject projectObject = projectProperty.get();
                    if(!optionEnum.isAllProject() && projectObject == null){
                        throw new Exception("Invalid project selection");
                    }
                    if(exportDestination.isBlank()){
                        throw new Exception("Invalid export location.");
                    }
                    updateMessage("Preparing to export...");

                    switch (optionEnum){
                        case ALL_UNIQUE_LIBRARIES:
                            exportAllUniqueLibraries();
                            break;
                        case ALL_LIBRARIES:
                            exportAllLibraries();
                            break;
                        case ALL_UNCLASSIFIED_UNIQUE_LIBRARIES:
                            exportAllUnclassifiedUniqueLibraries();
                            break;
                        case PROJECT_LIBRARIES:
                            exportAllProjectLibraries(projectObject);
                            break;
                        case PROJECT_UNIQUE_LIBRARIES:
                            exportProjectUniqueLibraries(projectObject);
                            break;
                        case PROJECT_UNCLASSIFIED_UNIQUE_LIBRARIES:
                            exportProjectUnclassifiedUniqueLibraries(projectObject);
                            break;
                        default:
                            throw new Exception("Invalid export option selected");
                    }
                    return null;
                }

                private void exportAllUniqueLibraries() throws Exception {
                    XSSFWorkbook workbook = new XSSFWorkbook();
                    exportService.exportAllUniqueLibraries(workbook);

                    //generating the excel file
                    String name = exportDestination+"/All_Unique_Libraries";
                    saveFile(name,workbook);
                }
                private void exportAllUnclassifiedUniqueLibraries() throws Exception {
                    XSSFWorkbook workbook = new XSSFWorkbook();
                    exportService.exportUniqueUnclassifiedLibraries(workbook);

                    //generating the excel file
                    String name = exportDestination+"/All_Unclassified_Unique_Libraries";
                    saveFile(name,workbook);
                }
                private void exportAllLibraries() throws Exception {
                    XSSFWorkbook workbook = new XSSFWorkbook();
                    exportService.exportAllLibraries(workbook);

                    //generating the excel file
                    String name = exportDestination+"/All_Libraries";
                    saveFile(name,workbook);
                }
                private void exportAllProjectLibraries(ProjectObject project) throws Exception {
                    XSSFWorkbook workbook = new XSSFWorkbook();
                    exportService.exportProjectLibrary(workbook,project.getId(),project.getName());

                    //generating the excel file
                    String name = exportDestination+"/_"+project.getName()+"_All_Libraries";
                    saveFile(name,workbook);
                }
                private void exportProjectUniqueLibraries(ProjectObject project) throws Exception {
                    XSSFWorkbook workbook = new XSSFWorkbook();
                    exportService.exportProjectUniqueLibraries(workbook,project.getId(),project.getName());

                    //generating the excel file
                    String name = exportDestination+"/_"+project.getName()+"_Unique_Libraries";
                    saveFile(name,workbook);
                }
                private void exportProjectUnclassifiedUniqueLibraries(ProjectObject project) throws Exception {
                    XSSFWorkbook workbook = new XSSFWorkbook();
                    exportService.exportProjectUnclassifiedUniqueLibraries(workbook, project.getId(), project.getName());

                    //generating the excel file
                    String name = exportDestination+"/_"+project.getName()+"_Unclassified_Unique_Libraries";
                    saveFile(name,workbook);
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
                   // super.failed();
                    Throwable throwable = getException();
                    throwable.printStackTrace();
                    errorViewModel.setError(throwable.getMessage());
                    updateMessage("Failed: "+throwable.getMessage());
                }
            };
        }
    };
    public ExportViewModel(ErrorViewModel errorViewModel, ProjectModel projectModel, EventHub eventHub, ExportService exportService){
        this.errorViewModel = errorViewModel;
        this.projectModel = projectModel;
        this.eventHub = eventHub;
        this.exportService = exportService;
        eventHub.subscribe(EventHub.EVENT_PROJECT_UPDATE,this,this::updateProjectEvent);
        updateProjectEvent("");
    }

    private void saveFile(String name, XSSFWorkbook workbook) throws IOException {
        File file = new File(name+".xlsx");
        int count = 0;
        while(file.exists()){
            count++;
            name = name+ count;
            file = new File(name+".xlsx");
        }

        FileOutputStream fileOut = new FileOutputStream(file);
        workbook.write(fileOut);
        fileOut.close();
    }

    public StringProperty optionProperty(){
        return optionProperty;
    }
    public ObjectProperty<ProjectObject> projectProperty(){
        return projectProperty;
    }
    public ReadOnlyStringProperty taskMessageProperty(){
        return exportProcess.messageProperty();
    }
    public ReadOnlyBooleanProperty taskRunningProperty(){
        return exportProcess.runningProperty();
    }
    public ReadOnlyDoubleProperty taskProgressProperty(){
        return exportProcess.progressProperty();
    }


    public ObjectProperty<ObservableList<ProjectObject>> projectsProperty(){
        return projectsProperty;
    }
    public void startProcess(){
        exportProcess.restart();
    }

    /**
     * Triggered by the event aggregator when an the projectModel update event is published
     * @param event
     */
    private void updateProjectEvent(String event){
        projectsProperty.setValue(FXCollections.observableArrayList(projectModel.getProjects()));
    }

    public void setExportDestination(String destination){
        this.exportDestination = destination;
    }

    }
