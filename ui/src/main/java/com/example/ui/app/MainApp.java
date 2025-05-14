package com.example.ui.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class MainApp extends Application {
    @Override
    public void init() {

    }

    @Override
    public void start(Stage stage) throws Exception {
        String fxmlPath = "/com/example/ui/app/main.fxml";
        URL fxmlUrl = getClass().getResource(fxmlPath);
        if(fxmlUrl == null) {
            throw new IllegalStateException("Cannot find FXML resource at " + fxmlPath);
        }
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        System.out.println("2");

        Parent root = loader.load();

        // Note that ui = loader.getController() only exists after we construct the FXML loader
        AppController app = new AppController(loader.getController());
        // Currently just a local variable but could promote to a member variable in MainApp if necessary
        app.initialize();

        stage.setScene(new Scene(root, 700, 500));
        stage.setTitle("Shortcut Reinforcement Agent");
        stage.show();
        System.out.println("3");
    }

    @Override
    public void stop() {

    }

    public static void main(String[] args) {
        // Calls init() then start()
        System.out.println("1");
        launch(args);
    }
}