package com.example.ui.controllers;

import com.example.core.sequence.InputSequence;
import com.example.core.utils.AppActionType;
import com.example.core.utils.Pair;
import com.example.core.utils.Suggestion;
import com.example.ui.components.AddSuggestionController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class HomeController extends BaseController {
    @FXML private TableView<Suggestion>        suggestionTable;
    @FXML private TableColumn<Suggestion, String> sequenceCol;
    @FXML private TableColumn<Suggestion, String> hintCol;
    @FXML private TableColumn<Suggestion, AppActionType> actionCol;

    private final ObservableList<Suggestion> suggestions = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configureTable();
        configureSequenceColumn();
        configureHintColumn();
        configureActionColumn();
        configureRowContextMenu();
    }

    private void configureTable() {
        suggestions.setAll(app.getEngine().getAllSuggestions());
        suggestionTable.setItems(suggestions);
        suggestionTable.setEditable(true);
    }

    private void configureSequenceColumn() {
        sequenceCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getSequence().toString())
        );
        sequenceCol.setCellFactory(TextFieldTableCell.forTableColumn());
        sequenceCol.setOnEditCommit(e -> {
            Suggestion s   = e.getRowValue();
            List<String> tokens = InputSequence.toTokenList(e.getNewValue());
            List<Pair<String, Optional<String>>> errors = InputSequence.parse(tokens);
            if(errors.isEmpty()) {
                app.getEngine().removeSuggestion(e.getRowValue().getSequence());
                s.setSequence(new InputSequence(tokens)); // Modify existing suggestion
                app.getEngine().putSuggestion(s);
                suggestionTable.refresh();
            } else {
                new Alert(Alert.AlertType.ERROR, InputSequence.getErrorString(errors)).showAndWait();
            }
        });
    }

    private void configureHintColumn() {
        hintCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTip())
        );
        hintCol.setCellFactory(TextFieldTableCell.forTableColumn());
        hintCol.setOnEditCommit(e -> {
            Suggestion s = e.getRowValue();
            s.setTip(e.getNewValue());
            app.getEngine().putSuggestion(s);
        });
    }

    private void configureActionColumn() {
        actionCol.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getActionType())
        );
        actionCol.setCellFactory(ComboBoxTableCell.forTableColumn(AppActionType.values()));
        actionCol.setOnEditCommit(e -> {
            Suggestion s             = e.getRowValue();
            AppActionType newType    = e.getNewValue();
            s.setActionType(newType);
            app.getEngine().putSuggestion(s);
        });

    }

    // Shows a delete option when user right-clicks a non-empty row
    private void configureRowContextMenu() {
        // Take a table view and return a table row
        suggestionTable.setRowFactory(tv -> {
            TableRow<Suggestion> row = new TableRow<>();
            ContextMenu menu = new ContextMenu();
            // Define what the delete "label" does
            MenuItem delete = new MenuItem("Delete");
            delete.setOnAction(a -> {
                Suggestion s = row.getItem();
                app.getEngine().removeSuggestion(s.getSequence());
                suggestions.remove(s);
            });
            menu.getItems().add(delete);

            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(menu)
            );
            return row;
        });
    }


    @FXML
    private void onAddEntry() {
        // Returns {sequence, hint}
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ui/components/AddSuggestionDialog.fxml"));
        DialogPane pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            System.err.println("Couldn't load for SOME reason.");
            return;
        }

        Dialog<Suggestion> d = new Dialog<>();
        d.setTitle("Add suggestion");
        d.setDialogPane(pane);

        d.setResultConverter(b -> {
            if(b.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                AddSuggestionController ctrl = loader.getController();
                List<String> tokens = InputSequence.toTokenList(ctrl.getSequence());
                List<Pair<String, Optional<String>>> errors = InputSequence.parse(tokens);
                if(errors.isEmpty()) {
                    return new Suggestion(new InputSequence(tokens), ctrl.getHint(), ctrl.getActionType());
                } else {
                    // Show alert and keep dialogue open
                    new Alert(Alert.AlertType.WARNING, InputSequence.getErrorString(errors));
                    return null;
                }
            }
            return null;
        });

        Optional<Suggestion> res = d.showAndWait();
        res.ifPresent(s -> {
            app.getEngine().putSuggestion(s);
            suggestions.remove(s);
        });
    }
}
