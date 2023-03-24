package com.webstudio;

import com.webstudio.view.ViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class WebStudio extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view.fxml"));
        Scene sc = new Scene(loader.load(), 1000, 800);
        sc.setFill(Color.TRANSPARENT);
        ViewController control = loader.getController();
        try(InputStream iconStream = getClass().getResourceAsStream("icons8-cell-65.png")) {
            Image image = new Image(Objects.requireNonNull(iconStream));
            stage.getIcons().add(image);
        } catch (Exception ignored) {
        }
        stage.setMinHeight(800);
        stage.setMinWidth(1000);
        stage.setScene(sc);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);
        control.init(stage);
        pushButton(control, stage);
        stage.show();
    }

    private void pushButton(ViewController control, Stage stage) {
        control.closeWindow(stage);
        control.minimizeWindow(stage);
        control.fullWindow(stage);
        control.turnWindow(stage);
        control.resizeWindow(stage);
        control.pusESC(stage);
        control.pushPlay();
    }

    public static void main(String[] args) {
        launch();
    }
}