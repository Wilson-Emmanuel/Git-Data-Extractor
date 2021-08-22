package com.softwarelab.dataextractor.mvvm.imports;

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
 * on Thu, 19/08/2021.
 */
@Component
public class ImportViewModel {

    private ErrorViewModel errorViewModel;
    private ProjectModel projectModel;
    private EventHub eventHub;

    private ObjectProperty<ObservableList<ProjectObject>> projectsProperty = new SimpleObjectProperty<>(FXCollections.emptyObservableList());


    private StringProperty urlPath = new SimpleStringProperty("");
    private StringProperty optionProperty = new SimpleStringProperty("");
    private ObjectProperty<ProjectObject> projectProperty = new SimpleObjectProperty<>(null);

    private Service<Void> importProcess = new Service<Void>() {
        @Override
        protected Task<Void> createTask() {
            return new Task<>() {
                @Override
                protected Void call() throws Exception {
                    updateMessage("");
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
    public ImportViewModel(ErrorViewModel errorViewModel, ProjectModel projectModel, EventHub eventHub){
        this.errorViewModel = errorViewModel;
        this.projectModel = projectModel;
        this.eventHub = eventHub;
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
        System.out.println("import updated");
        projectsProperty.setValue(FXCollections.observableArrayList(projectModel.getProjects()));
    }
}
