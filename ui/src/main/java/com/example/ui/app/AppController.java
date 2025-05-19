package com.example.ui.app;

import com.example.core.ShortcutEngine;
import com.example.ui.controllers.ReferencesController;
import com.example.ui.controllers.SuggestionsController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

public class AppController {
    @FXML private BorderPane rootView;
    private final ShortcutEngine engine;

    public AppController(ShortcutEngine engine) {
        this.engine = engine;
    }

    @FXML public void initialize() {
        showMainView();
    }

    public void showMainView() {
        loadCenter(
                "/com/example/ui/controllers/SuggestionsView.fxml",
                SuggestionsController.class,
                ctrl -> ctrl.setAppController(this)
        );
    }

    public void showReferences() {
        loadCenter(
                "/com/example/ui/controllers/ReferencesView.fxml",
                ReferencesController.class,
                ctrl -> ctrl.setAppController(this)
        );
    }

    private <T> void loadCenter(String fxmlPath,
                                Class<T> controllerType,
                                Consumer<T> init)
    {
        try {
            URL url = getClass().getResource(fxmlPath);
            System.out.println("Loading FXML at path: " + fxmlPath + " → URL: " + url);
            if (url == null) {
                throw new IllegalStateException("FXML not found at: " + fxmlPath);
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent view = loader.load();

            T ctrl = controllerType.cast(loader.getController());
            init.accept(ctrl);
            rootView.setCenter(view);
        } catch (IOException e) {
            System.err.println("Failed to load " + fxmlPath);
            e.printStackTrace();
            Platform.runLater(() ->
                    new Alert(Alert.AlertType.ERROR,
                            "Could not load view: " + fxmlPath)
                            .showAndWait()
            );
        }
    }

    public ShortcutEngine getEngine() {
        return engine;
    }
}
