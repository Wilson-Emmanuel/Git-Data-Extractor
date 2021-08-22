package com.softwarelab.dataextractor.mvvm.extract;

import com.softwarelab.dataextractor.core.services.usecases.ProjectService;
import com.softwarelab.dataextractor.mvvm.errors.ErrorViewModel;
import javafx.beans.property.*;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.stage.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Wilson
 * on Mon, 16/08/2021.
 */
@Component
public class ExtractViewModel {

    @Autowired
    private ErrorViewModel errorViewModel;
    @Autowired
    private ProjectService projectService;

    private StringProperty urlOrPath = new SimpleStringProperty("");


    private Service<Void> extractionProcess = new Service<Void>() {
        @Override
        protected Task<Void> createTask() {

            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    updateMessage("Start processing");

                    errorViewModel.setError("Just testing");
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

    public StringProperty urlOrPathProperty(){
        return urlOrPath;
    }

    public ReadOnlyStringProperty taskMessageProperty(){
        return extractionProcess.messageProperty();
    }
    public ReadOnlyBooleanProperty taskRunningProperty(){
        return extractionProcess.runningProperty();
    }
    public ReadOnlyDoubleProperty taskProgressProperty(){
        return extractionProcess.progressProperty();
    }

    public void startTask(){
        extractionProcess.restart();
    }
    public void cancelTask(){
        if(extractionProcess.isRunning())
            extractionProcess.cancel();
    }


}
