package com.java.scientific;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ScientificCalc extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        System.setProperty("prism.lcdtext", "false");
        FXMLLoader fxmlLoader = new FXMLLoader(ScientificCalc.class.getResource("simpleOperations.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        try {
            scene.getStylesheets().add(Objects.requireNonNull(ScientificCalc.class.getResource("darcula.css")).toExternalForm());
            System.out.println("Successfully set up stylesheet");
        } catch (NullPointerException e) {
            System.out.println("Could not set up stylesheet");
        }

        try {
            String path = Objects.requireNonNull(ScientificCalc.class.getResource("icon.png")).toExternalForm();
            stage.getIcons().add(new Image(path));
            System.out.println("Successfully set up icon");
        } catch (NullPointerException e) {
            System.out.println("Could not set up icon");
        }

        stage.setTitle("Scientific Calculator");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
