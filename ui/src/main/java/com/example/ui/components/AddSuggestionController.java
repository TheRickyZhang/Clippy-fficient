package com.example.ui.components;

import com.example.core.utils.AppActionType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class AddSuggestionController {
    @FXML private TextField sequenceField;
    @FXML private TextField hintField;
    @FXML private ComboBox<AppActionType> actionField;

    @FXML private ButtonType okButton;
    @FXML private ButtonType cancelButton;

    @FXML private void initialize() {
        actionField.setItems(FXCollections.observableArrayList(AppActionType.values()));
        actionField.setValue(AppActionType.OPEN_POPUP);
    }

    // TODO: Get feedback on submission (check the shortcut syntax)

    public String getSequence() {
        return sequenceField.getText();
    }
    public String getHint() {
        return hintField.getText();
    }
    /**
     * Turn the user’s choice into the actual Consumer<String> that core will call.
     */
    public AppActionType getActionType() {
        return actionField.getValue();
    }


}
