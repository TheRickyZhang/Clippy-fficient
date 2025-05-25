package com.example.ui.controllers;

import com.example.ui.app.AppController;
import javafx.fxml.FXML;

public abstract class BaseController {
    protected AppController app;

    public BaseController() {
    }

    public void setAppController(AppController app) {
        this.app = app;
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
