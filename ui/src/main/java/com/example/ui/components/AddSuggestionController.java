package com.example.ui.components;

import com.example.shared.AppAction;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddSuggestionController {
    @FXML public Label   errorLabel;
    @FXML public TextField sequenceField;
    @FXML public TextField hintField;
    @FXML public ComboBox<AppAction> actionField;
    @FXML public Button  addBtn;
    @FXML public Button  closeBtn;

    @FXML
    private void initialize() {
        actionField.setItems(FXCollections.observableArrayList(AppAction.values()));
        actionField.getSelectionModel().selectFirst();
        errorLabel.setVisible(false);
    }

    public String getSequence()      { return sequenceField.getText(); }
    public String getHint()          { return hintField.getText(); }
    public AppAction getActionType() { return actionField.getValue(); }

    public void clearFields() {
        sequenceField.clear();
        hintField.clear();
        actionField.getSelectionModel().selectFirst();
        errorLabel.setVisible(false);
    }
}
