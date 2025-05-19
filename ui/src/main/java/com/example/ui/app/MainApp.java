package com.example.ui.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

// Entry point of the entire app
public class MainApp extends Application {
    @Override
    public void init() {

    }

    @Override
    public void start(Stage stage) throws Exception {
        // Entry point for the UI
        String fxmlPath = "/com/example/ui/app/main.fxml";
        URL fxmlUrl = getClass().getResource(fxmlPath);
        if(fxmlUrl == null) {
            throw new IllegalStateException("Cannot find FXML resource at " + fxmlPath);
        }

        // Calls all the FXML methods and loads the root of the scene graph
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        // Our own app-specific initialization based on the FXML instance
        AppController app = new AppController(loader.getController());
        app.initialize();

        stage.setScene(new Scene(root, 700, 500));
        stage.setTitle("Shortcut Reinforcement Agent");
        stage.show();
        System.out.println("done with start");
    }

    @Override
    public void stop() {

    }

    public static void main(String[] args) {
        // Calls init() then start()
        launch(args);
    }
}