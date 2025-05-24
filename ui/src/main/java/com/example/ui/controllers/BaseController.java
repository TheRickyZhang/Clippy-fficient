package com.example.ui.controllers;

import com.example.core.ShortcutEngine;
import com.example.ui.app.AppController;
import javafx.fxml.FXML;

public abstract class BaseController {
    protected AppController app;
    protected ShortcutEngine engine;

    public BaseController() {
    }

    public void setAppController(AppController app) {
        this.app = app;
        this.engine = app.getEngine();
    }

    @FXML
    private void onShowMain() {
        app.showMainView();
    }

    @FXML
    private void onShowReferences() {
        app.showReferencesView();
    }

    @FXML
    private void onShowLog() {
        app.showLogView();
    }
}
