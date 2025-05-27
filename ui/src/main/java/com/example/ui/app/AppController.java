package com.example.ui.app;

import com.example.core.ShortcutEngine;
import com.example.core.search.SearchEntry;
import com.example.core.search.SearchService;
import com.example.core.tokens.TokenRegistry;
import com.example.ui.components.HeaderController;
import com.example.ui.controllers.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class AppController {
    @FXML
    private HeaderController header;
    @FXML
    private BorderPane rootView;

    private final ShortcutEngine engine;
    private final KeyCombination logKeybind = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN);
    private final KeyCombination referencesKeybind = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);

    // Stores each page’s scene graph so we don’t reload FXML on every switch
    private final Map<Page, Parent> pageViews = new EnumMap<>(Page.class);

    // Properties
    private final BooleanProperty signedIn = new SimpleBooleanProperty(false);

    // Controllers
    HomeController homeController;
    ReferencesController referencesController;
    LogController logController;
    SigninController signinController;

    public AppController(ShortcutEngine engine) {
        this.engine = engine;
    }

    /**
     * Called by FXMLLoader once @FXML‐injection is done.
     */
    @FXML
    private void initialize() {
        // Let the header buttons / search icon know how to talk back to us
        header.setAppController(this);

        //Load and cache ALL pages (even if user never visits them)
        preloadAllPages();
        showPage(Page.SUGGESTIONS);

        header.signedInProperty().bind(signedInProperty());

        initSearch();
    }
    public ShortcutEngine getEngine() {
        return engine;
    }

    private void preloadAllPages() {
        for (Page p : Page.values()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(p.getFxmlPath()));
                loader.setControllerFactory(type -> {
                    try {
                        BaseController ctrl = (BaseController) type.getDeclaredConstructor().newInstance();
                        ctrl.setAppController(this);
                        return ctrl;
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                });

                Parent view       = loader.load();
                BaseController c  = loader.getController();

                p.install(this, c);

                pageViews.put(p, view);
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Failed to load “" + p + "”: " + e.getMessage())
                        .showAndWait();
                e.printStackTrace();
            }
        }
    }


    public void showPage(Page p) {
        Parent view = pageViews.get(p);
        if (view == null) {
            new Alert(Alert.AlertType.ERROR, "No view for page " + p).showAndWait();
        } else {
            rootView.setCenter(view);
        }
    }

    // Set up search providers
    // TODO: Set up for other pages
    private void initSearch() {
        SearchService svc = SearchService.get();
        svc.registerProvider("References", () ->
                TokenRegistry.getTokenMapValues().stream()
                        .map(e -> new SearchEntry(
                                "References",
                                e.getKey(),
                                () -> {
                                    showReferencesView();
                                    // Now that we know referencesController is non-null, select the token
                                    referencesController.selectToken(e.getKey());
                                }))
                        .toList()
        );
    }

    // Properties
    public BooleanProperty signedInProperty() {
        return signedIn;
    }
    public void markSignedIn() {
        signedIn.set(true);
        showPage(Page.SUGGESTIONS);
    }
    public void markSignedOut() {
        signedIn.set(false);
        showPage(Page.SIGNIN);
    }

    // Controllers
    public void showMainView()       { showPage(Page.SUGGESTIONS); }
    public void showReferencesView() { showPage(Page.REFERENCES); }
    public void showLogView()        { showPage(Page.LOG); }
    public void showSigninView()     { showPage(Page.SIGNIN); }
}
