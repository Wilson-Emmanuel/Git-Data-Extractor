package com.softwarelab.dataextractor;

import com.softwarelab.dataextractor.mvvm.export.ExportView;
import com.softwarelab.dataextractor.mvvm.extract.ExtractView;
import com.softwarelab.dataextractor.mvvm.extract.ExtractViewModel;
import com.softwarelab.dataextractor.mvvm.imports.ImportView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Wilson
 * on Tue, 10/08/2021.
 */
@Component
public class MainView implements Initializable {

    @FXML
    private Tab extractionTab;
    @FXML
    private Tab exportTab;
    @FXML
    private Tab importTab;

    @Autowired
    private ExportView exportView;
    @Autowired
    private ImportView importView;
    @Autowired
    private ExtractView extractView;
    @Autowired
    private ExtractViewModel extractViewModel;

    @Value("classpath:/export.fxml")
    private Resource exportResource;
    @Value("classpath:/import.fxml")
    private Resource importResource;
    @Value("classpath:/extract.fxml")
    private Resource extractResource;

    @SneakyThrows
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        FXMLLoader extractLoader = new FXMLLoader(extractResource.getURL());
        extractLoader.setController(extractView);
        Parent extractParent = extractLoader.load();

        FXMLLoader importLoader = new FXMLLoader(importResource.getURL());
        importLoader.setController(importView);
        Parent importParent = importLoader.load();

        FXMLLoader exportLoader = new FXMLLoader(exportResource.getURL());
        exportLoader.setController(exportView);
        Parent exportParent = exportLoader.load();

        extractionTab.setContent(extractParent);
        importTab.setContent(importParent);
        exportTab.setContent(exportParent);

        //importTab.disableProperty().bind(extractViewModel.taskRunningProperty());
        //exportTab.disableProperty().bind(extractViewModel.taskRunningProperty());
    }
}
