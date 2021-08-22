package com.softwarelab.dataextractor.core.utilities;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Window;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
public class NotificationUtil {
    public static void inputError(String error){
        Notifications.create()
                .title("Bad Input")
                .text(error)
                .darkStyle()
                .hideAfter(Duration.seconds(3))
                .position(Pos.BOTTOM_RIGHT)
                .showError();
    }
    public static void warning(String warning){
        Notifications.create()
                .title("Warning")
                .text(warning)
                .darkStyle()
                .hideAfter(Duration.seconds(3))
                .position(Pos.BOTTOM_RIGHT)
                .showWarning();
    }
    public static void success(String message){
        Image  image = new Image("/images/good.png");

        Notifications.create()
                .title("Success")
                .text(message)
                .darkStyle()
                .graphic(new ImageView(image))
                .hideAfter(Duration.seconds(3))
                .position(Pos.BOTTOM_RIGHT)
                .showInformation();
    }
    public static void showAlert(Alert.AlertType type, String title, String content){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        //alert.initModality(Modality.WINDOW_MODAL);
        //alert.initOwner(owner);
        alert.show();
    }
}
