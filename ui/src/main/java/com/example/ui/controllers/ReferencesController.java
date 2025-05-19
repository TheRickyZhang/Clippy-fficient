package com.example.ui.controllers;

import com.example.core.sequence.InputSequence;
import com.example.ui.app.AppController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class ReferencesController {
    @FXML private ListView<String> tokenList;

    private AppController app;

    public void setAppController(AppController app) {
        this.app = app;
    }

    @FXML
    public void initialize() {
        tokenList.setItems(
                FXCollections.observableArrayList(InputSequence.getTokens())
        );
    }

    @FXML
    private void onBack() {
        app.showMainView();
    }
}
