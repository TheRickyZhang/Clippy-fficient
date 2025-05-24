package com.example.ui.app;

import com.example.core.ShortcutEngine;
import com.example.core.search.SearchEntry;
import com.example.core.search.SearchService;
import com.example.core.tokens.TokenRegistry;
import com.example.ui.components.HeaderController;
import com.example.ui.controllers.LogController;
import com.example.ui.controllers.ReferencesController;
import com.example.ui.controllers.SuggestionsController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

public class AppController {
    @FXML
    private HeaderController header;
    @FXML
    private BorderPane rootView;
    private final ShortcutEngine engine;
    private final KeyCombination logKeybind;
    private final KeyCombination referencesKeybind;

    private LogController logController;
    private ReferencesController referencesController;
    private SuggestionsController suggestionsController;

    public AppController(ShortcutEngine engine) {
        this.engine = engine;
        this.logKeybind = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN);
        this.referencesKeybind = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
    }

    @FXML
    public void initialize() {
        header.setAppController(this);
        initSearch();
        showMainView();
    }


    private <T> void loadCenter(String fxmlPath,
                                Class<T> controllerType,
                                Consumer<T> init) {
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

    // TODO: Add more application-specific shortcuts - maybe hard code into shortcut detection?
    public void registerGlobalShortcuts(Scene scene) {
        scene.getAccelerators().put(this.referencesKeybind, this::showReferencesView);
        scene.getAccelerators().put(this.logKeybind, this::showLogView);
    }

    // Transition between windows (show + {NAME} + View())
    public void showMainView() {
        loadCenter(
                "/com/example/ui/controllers/SuggestionsView.fxml",
                SuggestionsController.class,
                ctrl ->  {
                    ctrl.setAppController(this);
                    this.suggestionsController = ctrl;
                }
        );
    }

    public void showReferencesView() {
        loadCenter(
                "/com/example/ui/controllers/ReferencesView.fxml",
                ReferencesController.class,
                ctrl -> {
                    ctrl.setAppController(this);
                    this.referencesController = ctrl;
                }
        );
    }

    public void showLogView() {
        loadCenter(
                "/com/example/ui/controllers/LogView.fxml",
                LogController.class,
                ctrl -> {
                  ctrl.setAppController(this);
                  this.logController = ctrl;
                }
        );
    }

    private void initSearch() {
        SearchService s = SearchService.get();
//        s.get().registerProvider("Main", () -> {
//            System.out.println("Going to main page");
//            showMainView();
//        })).toList();

        s.registerProvider("References", () ->
                // for each token/key in your registry…
                TokenRegistry.getTokenMapValues().stream()
                        .map(e -> new SearchEntry(
                                /* page */   "References",
                                /* display */e.getKey(),
                                /* action: */() -> {
                            // when the user double-clicks this entry:
                            referencesController.selectToken(e.getKey());
                        }))
                        .toList()
        );

    }

    // TODO: open new window on top (popup) - is this the right place?
}
