package com.softwarelab.dataextractor;

import com.softwarelab.dataextractor.core.utilities.NotificationUtil;
import com.softwarelab.dataextractor.ui.view_controller.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * Created by Wilson
 * on Sun, 20/12/2020.
 */
@Component
public class StageInitializer implements ApplicationListener<GitJavaDataExtractorUIStarter.StageReadyEvent> {

    @Value("classpath:/main.fxml")
    private Resource mainResource;

    @Value("${application.stage.title}")
    private String appTitle;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MainController mainController;

    private Stage mainStage;
    @Override
    public void onApplicationEvent(GitJavaDataExtractorUIStarter.StageReadyEvent stageReadyEvent) {

        setDefaultExceptionHandler();
        try{
           FXMLLoader fxmlLoader = new FXMLLoader(mainResource.getURL());
            fxmlLoader.setController(mainController);
            Parent parent = fxmlLoader.load();
            parent.getStyleClass().add("mainbg");

            mainStage = stageReadyEvent.getStage();

            Scene scene = new Scene(parent);
            scene.getStylesheets().add("/main.css");

            //Screen screen = Screen.getPrimary();
            //double width = screen.getVisualBounds().getWidth();
            //double height = screen.getVisualBounds().getHeight();
            mainStage.setScene(scene);
            mainStage.setWidth(835);
            mainStage.setHeight(675);
            mainStage.setTitle(appTitle);
            mainStage.setResizable(false);
            mainStage.getIcons().add(new Image("/data.png"));
            mainStage.centerOnScreen();
            mainStage.show();
        }catch (Exception ex){
            //ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Application Error");
            alert.setContentText("An error occurred while starting the application. Please try again " +
                    "or contact the developer.");
            alert.show();
        }
    }
/*
Global Exception Handler
 */
    private void setDefaultExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler( (thread, throwable) ->{
            throwable.printStackTrace();
            NotificationUtil.inputError("Error: "+throwable.getMessage());
        });
        /*
        For current thread
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            System.out.println("Handler caught exception: "+throwable.getMessage());
        });
         */
    }
}
