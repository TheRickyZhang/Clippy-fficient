package com.example.ui.app;

import com.example.agent.GlobalHookService;
import com.example.core.ShortcutEngine;
import com.example.core.context.ApplicationContext;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        ApplicationContext ctx    = new ApplicationContext(List.of());
        ShortcutEngine    engine = new ShortcutEngine();
        GlobalHookService.start(engine::onEvent);

        URL fxmlUrl = getClass().getResource("/com/example/ui/app/AppView.fxml");
        System.out.println("Loading FXML from: " + fxmlUrl);
        if (fxmlUrl == null) {
            throw new IllegalStateException("Couldn’t find AppView.fxml on the classpath!");
        }
        FXMLLoader loader = new FXMLLoader(fxmlUrl);


        loader.setControllerFactory(type -> {
            if (type == AppController.class) {
                return new AppController(engine);
            }
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        Parent root;
        try {
            root = loader.load();
            System.out.println("FXML loaded, controller = " + loader.getController());
        } catch (Exception e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            throw e;
        }

        stage.setScene(new Scene(root, 700, 500));
        stage.setTitle("Shortcut Reinforcement Agent");
        stage.show();
    }

    @Override
    public void stop() {
       GlobalHookService.stop();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
