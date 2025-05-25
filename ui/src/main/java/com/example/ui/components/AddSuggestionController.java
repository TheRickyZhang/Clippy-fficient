package com.example.ui.components;

import com.example.core.utils.AppAction;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
        errorLabel.setVisible(false);
    }

    public String getSequence()      { return sequenceField.getText(); }
    public String getHint()          { return hintField.getText(); }
    public AppAction getActionType() { return actionField.getValue(); }

    public void clearFields() {
        sequenceField.clear();
        hintField.clear();
        actionField.getSelectionModel().clearSelection();
        errorLabel.setVisible(false);
    }
}
