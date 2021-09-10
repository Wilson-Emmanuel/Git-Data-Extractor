package com.softwarelab.dataextractor.mvvm.models;

import com.softwarelab.dataextractor.core.persistence.models.ProjectObject;
import com.softwarelab.dataextractor.mvvm.event_aggregator.EventHub;
import com.softwarelab.dataextractor.core.services.usecases.ProjectService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Created by Wilson
 * on Thu, 19/08/2021.
 */
@Component
public class ProjectModel {


    private ProjectService projectService;
    private EventHub eventHub;

    public ProjectModel(ProjectService projectService, EventHub eventHub){
        this.projectService = projectService;
        this.eventHub = eventHub;
        projects = projectService.getAllProjects();
    }


    private List<ProjectObject> projects = Collections.emptyList();

    public void updateProjects(){
        projects = projectService.getAllProjects();
//        ProjectObject projectObject = ProjectObject.builder()
//                .id(2L)
//                .localPath("anything again2")
//                .remoteURL("anything again2")
//                .name("Project name2")
//                .localPath("localpath2")
//                .build();
        //projects.add(projectObject);
        eventHub.publish(EventHub.EVENT_PROJECT_UPDATE);
    }
    public List<ProjectObject> getProjects(){
        return projects;
    }


}
