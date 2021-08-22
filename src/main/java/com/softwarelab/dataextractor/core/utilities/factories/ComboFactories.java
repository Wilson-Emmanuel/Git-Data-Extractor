package com.softwarelab.dataextractor.core.utilities.factories;

import com.softwarelab.dataextractor.core.persistence.models.ProjectObject;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.springframework.stereotype.Component;

/**
 * Created by Wilson
 * on Sun, 22/08/2021.
 */
@Component
public class ComboFactories {

    public  Callback<ListView<ProjectObject>, ListCell<ProjectObject>> getProjectComboCellFactory(){
        return new Callback<>() {
            @Override
            public ListCell<ProjectObject> call(ListView<ProjectObject> projectObjectListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(ProjectObject projectObject, boolean b) {
                        super.updateItem(projectObject, b);
                        if (projectObject == null || b) {
                            setText("Select Project");
                        } else
                            setText(projectObject.getName());
                    }
                };
            }
        };

    }
}
