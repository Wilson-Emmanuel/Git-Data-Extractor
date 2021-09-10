package com.softwarelab.dataextractor.mvvm.errors;

import com.softwarelab.dataextractor.core.utilities.NotificationUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.stage.Window;
import org.springframework.stereotype.Component;

/**
 * Created by Wilson
 * on Wed, 18/08/2021.
 */
@Component
public class ErrorViewModel {

    private StringProperty error = new SimpleStringProperty("");

    public ErrorViewModel(){
        error.addListener((observableValue, s, t1) -> {
            Platform.runLater(() -> NotificationUtil.showAlert(Alert.AlertType.ERROR,"Error",t1));
        });
    }
    public void setError(String errorMessage){
        error.setValue(errorMessage);
    }
}
