package com.softwarelab.dataextractor.core.utilities.converters;

import com.softwarelab.dataextractor.core.persistence.models.ProjectObject;
import javafx.util.StringConverter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wilson
 * on Sun, 22/08/2021.
 */
@Component
public class CustomStringConverters {

    public  StringConverter<ProjectObject> getProjectObjectStringConverter(){

        return new StringConverter<>() {
            Map<String, ProjectObject> converted = new HashMap<>();

            @Override
            public String toString(ProjectObject projectObject) {
                converted.put(projectObject.getName(), projectObject);
                return projectObject.getName();
            }

            @Override
            public ProjectObject fromString(String s) {
                return converted.get(s);
            }
        };
    }
}
