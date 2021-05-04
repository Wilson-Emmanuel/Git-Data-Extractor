package com.softwarelab.dataextractor.core.services.processors;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;

/**
 * Created by Wilson
 * on Tue, 04/05/2021.
 */
public abstract class ProgressUpdator {
    protected SimpleStringProperty message = new SimpleStringProperty("");
    protected SimpleDoubleProperty total = new SimpleDoubleProperty(0.0);
    protected SimpleDoubleProperty runningTotal = new SimpleDoubleProperty(0.0);

    protected void bindListener(ChangeListener<String> messageListener, ChangeListener<Number> totalListener, ChangeListener<Number> runningTotalListener){
        total.addListener(totalListener);
        runningTotal.addListener(runningTotalListener);
        message.addListener(messageListener);
    }
    protected void unbindListener(ChangeListener<String> messageListener, ChangeListener<Number> totalListener, ChangeListener<Number> runningTotalListener){
        total.removeListener(totalListener);
        runningTotal.removeListener(runningTotalListener);
        message.removeListener(messageListener);
    }
}
