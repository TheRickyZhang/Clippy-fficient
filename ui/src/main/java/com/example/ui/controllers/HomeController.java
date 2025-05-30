package com.example.ui.controllers;

import com.example.core.sequence.DSLParser;
import com.example.core.sequence.InputSequence;
import com.example.core.sequence.ParseException;
import com.example.core.sequence.SequenceElement;
import com.example.core.utils.Suggestion;
import com.example.shared.AppAction;
import com.example.ui.components.AddSuggestionController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class HomeController extends BaseController {
    @FXML
    private TableView<Suggestion> suggestionTable;
    @FXML
    private TableColumn<Suggestion, String> sequenceCol;
    @FXML
    private TableColumn<Suggestion, String> hintCol;
    @FXML
    private TableColumn<Suggestion, AppAction> actionCol;

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
            Suggestion s = e.getRowValue();
            try {
               List<SequenceElement> steps = DSLParser.parse(e.getNewValue());
                s.setSequence(new InputSequence(steps));
                app.getEngine().putSuggestion(s);
                suggestionTable.refresh();
            } catch (ParseException ex) {
                new Alert(Alert.AlertType.ERROR, ex.toString()).showAndWait();
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
        actionCol.setCellFactory(ComboBoxTableCell.forTableColumn(AppAction.values()));
        actionCol.setOnEditCommit(e -> {
            Suggestion s = e.getRowValue();
            AppAction newType = e.getNewValue();
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
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/ui/components/AddSuggestionDialog.fxml")
            );
            Parent root = loader.load();
            AddSuggestionController ctrl = loader.getController();

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(suggestionTable.getScene().getWindow());
            dialog.setTitle("Add suggestion");
            dialog.setScene(new Scene(root));

            // Add button tried to add an entry, but doesn't close the window
            ctrl.addBtn.setOnAction(evt -> {
                try {
                    List<SequenceElement> steps = DSLParser.parse(ctrl.getSequence());
                    Suggestion s = new Suggestion(
                            new InputSequence(steps),
                            ctrl.getHint(),
                            ctrl.getActionType()
                    );
                    app.getEngine().putSuggestion(s);
                    suggestions.add(s);
                    ctrl.clearFields();
                } catch (ParseException ex) {
                    ctrl.errorLabel.setText(ex.toString());
                    ctrl.errorLabel.setVisible(true);
                }
            });
            ctrl.closeBtn.setOnAction(evt -> dialog.close());
            dialog.show();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load dialog:\n" + e.getMessage()).showAndWait();
        }
    }
}
