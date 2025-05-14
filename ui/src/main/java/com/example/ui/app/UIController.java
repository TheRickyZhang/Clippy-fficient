package com.example.ui.app;

import com.example.core.SuggestionRegistry;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class UIController {
    @FXML private TableView<Suggestion> suggestionTable;
    @FXML private TableColumn<Suggestion,String> patternCol;
    @FXML private TableColumn<Suggestion,String> hintCol;
    @FXML private TableColumn<Suggestion,Void> actionCol;
    @FXML private TextArea logArea;

    private AppController app;

    /** Called by MainApp after loading */
    public void setAppController(AppController app) {
        this.app = app;
    }

    @FXML
    public void initialize() {
        // Populate table
        ObservableList<Suggestion> items = FXCollections.observableArrayList();
        SuggestionRegistry.all().forEach((p, h) -> items.add(new Suggestion(p, h)));
        suggestionTable.setItems(items);

        // Configure columns
        patternCol.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().pattern().toString())
        );
        hintCol.setCellValueFactory(new PropertyValueFactory<>("hint"));

        // Action column
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Delete");
            {
                btn.setOnAction(evt -> {
                    Suggestion s = getTableView().getItems().get(getIndex());
                    app.removePattern(s.pattern());
                    getTableView().getItems().remove(s);
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    @FXML
    private void onAddHintClicked() {
        app.showAddDialog(suggestionTable);
    }

    /** Invoked by AppController when a hint fires */
    public void appendLog(String line) {
        Platform.runLater(() -> logArea.appendText(line + "\n"));
    }
}