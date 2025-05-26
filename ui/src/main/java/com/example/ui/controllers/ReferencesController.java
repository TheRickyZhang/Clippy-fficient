package com.example.ui.controllers;

import com.example.core.tokens.TokenRegistry;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.Locale;
import java.util.Map;

public class ReferencesController extends BaseController {
    @FXML
    private TableView<Map.Entry<String, Integer>> tableView;
    @FXML
    private TableColumn<Map.Entry<String, Integer>, String> tokenCol;
    @FXML
    private TableColumn<Map.Entry<String, Integer>, String> propsCol;
    @FXML
    private TextField filterReferences;

    @FXML
    public void initialize() {
        // Assume TokenRegistry.getTokenMapValues() now returns List<Map.Entry<String,Integer>>
        ObservableList<Map.Entry<String, Integer>> data = FXCollections
                .observableArrayList(TokenRegistry.getTokenMapValues());

        // wrap in a FilteredList (initial predicate = true, show all)
        FilteredList<Map.Entry<String, Integer>> filtered = new FilteredList<>(data, e -> true);

        // listen for text changes in filterReferences
        filterReferences.textProperty().addListener((obs, oldVal, newVal) -> {
            String lc = (newVal == null ? "" : newVal.toLowerCase(Locale.ROOT));
            filtered.setPredicate(entry -> {
                if (lc.isEmpty()) return true;
                // match on key or value
                if (entry.getKey().toLowerCase(Locale.ROOT).contains(lc)) return true;
                return entry.getValue()
                        .toString()
                        .toLowerCase(Locale.ROOT)
                        .contains(lc);
            });
        });

        // preserve sort order when filtering
        SortedList<Map.Entry<String, Integer>> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sorted);

        // column for the map key
        tokenCol.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getKey())
        );
        // column for the map value (as String)
        propsCol.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getValue().toString())
        );
        tokenCol.setSortType(TableColumn.SortType.ASCENDING);
        tableView.getSortOrder().add(tokenCol);
        tableView.sort();
    }

    /**
     * Call this when someone “search-activated” a token.
     */
    public void selectToken(String token) {
        app.showReferencesView();
        filterReferences.setText(token);

        // highlight the matching entry by key
        tableView.getItems().stream()
                .filter(e -> e.getKey().equals(token))
                .findFirst()
                .ifPresent(item -> {
                    tableView.getSelectionModel().select(item);
                    tableView.scrollTo(item);
                });
    }
}
