package com.example.ui.components;

import com.example.ui.app.AppController;
import com.example.ui.search.SearchDialog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ToolBar;

import java.io.IOException;

public class HeaderController extends ToolBar {
    private SearchDialog searchDialog;
    private AppController app;

    public HeaderController() {
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("HeaderView.fxml"));
        fxml.setRoot(this);
        fxml.setController(this);
        try {
            fxml.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        searchDialog = new SearchDialog();
    }


    public void setAppController(AppController app) {
        this.app = app;
        // TODO: bind search
    }

    @FXML private void onBack()           { app.showMainView(); }
    @FXML private void onShowMain()       { app.showMainView(); }
    @FXML private void onShowReferences() { app.showReferencesView(); }
    @FXML private void onShowLog()        { app.showLogView(); }
    @FXML private void onSearchOpen() {
        searchDialog.open();
    }
}
