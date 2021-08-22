package com.softwarelab.dataextractor.mvvm.export;

import com.softwarelab.dataextractor.core.persistence.models.ProjectObject;
import com.softwarelab.dataextractor.mvvm.errors.ErrorViewModel;
import com.softwarelab.dataextractor.mvvm.event_aggregator.EventHub;
import com.softwarelab.dataextractor.mvvm.models.ProjectModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.springframework.stereotype.Component;

/**
 * Created by Wilson
 * on Wed, 18/08/2021.
 */
@Component
public class ExportViewModel {


    private ErrorViewModel errorViewModel;
    private ProjectModel projectModel;
    private EventHub eventHub;

    private ObjectProperty<ObservableList<ProjectObject>> projectsProperty = new SimpleObjectProperty<>(FXCollections.emptyObservableList());

   private StringProperty optionProperty = new SimpleStringProperty("");
   private ObjectProperty<ProjectObject> projectProperty = new SimpleObjectProperty<>(null);

    private Service<Void> exportProcess = new Service<Void>() {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    projectModel.updateProjects();
                    return null;
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
                    updateMessage("Failed!");
                }
            };
        }
    };
    public ExportViewModel(ErrorViewModel errorViewModel, ProjectModel projectModel, EventHub eventHub){
        this.errorViewModel = errorViewModel;
        this.projectModel = projectModel;
        this.eventHub = eventHub;
        eventHub.subscribe(EventHub.EVENT_PROJECT_UPDATE,this,this::updateProjectEvent);
        updateProjectEvent("");
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
        System.out.println("export updated");
        projectsProperty.setValue(FXCollections.observableArrayList(projectModel.getProjects()));
    }

    }
