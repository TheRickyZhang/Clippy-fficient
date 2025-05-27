package com.example.ui.components;

import com.example.ui.app.AppController;
import com.example.ui.app.Page;
import com.example.ui.search.SearchDialog;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;

import java.io.IOException;

public class HeaderController extends ToolBar {
    @FXML private Button authButton;
    private SearchDialog searchDialog;
    private AppController app;

    // Properties
    private final BooleanProperty signedIn = new SimpleBooleanProperty();

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
        // Inject sign in / sign out depending on if signed in
        authButton.textProperty().bind(Bindings.when(signedIn).then("Sign out").otherwise("Log in"));
        authButton.setOnAction(e -> {
            if(signedIn.get()) app.markSignedOut();
            else app.showPage(Page.SIGNIN);
        });
    }


    public void setAppController(AppController app) {
        this.app = app;
        signedIn.bind(app.signedInProperty());
        // TODO: bind search
    }

    // Properties
    public BooleanProperty signedInProperty() {
        return signedIn;
    }

    @FXML private void onBack()           { app.showMainView(); }
    @FXML private void onShowMain()       { app.showMainView(); }
    @FXML private void onShowReferences() { app.showReferencesView(); }
    @FXML private void onShowLog()        { app.showLogView(); }
    @FXML private void onSearchOpen() {
        new SearchDialog().open();
    }
}
