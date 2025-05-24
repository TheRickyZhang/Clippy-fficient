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

import java.util.List;
import java.util.Locale;

public class ReferencesController extends BaseController {
    @FXML
    private TableView<RefItem> tableView;
    @FXML
    private TableColumn<RefItem, String> tokenCol;
    @FXML
    private TableColumn<RefItem, String> propsCol;
    @FXML
    private TextField filterReferences;

    // RefItem = .token | {.props[0], .props[1], ... }
    public record RefItem(String token, List<String> props) {
        public String getJoinedProps() {
            return String.join(", ", props);
        }
    }

    @FXML
    public void initialize() {
        ObservableList<RefItem> data = FXCollections.observableArrayList(TokenRegistry.getTokenMapValues().stream()
                .map(e -> new RefItem(e.getKey(), e.getValue()))
                .toList());

        // Filtered list for searching, initially show everything
        FilteredList<RefItem> filtered = new FilteredList<>(data, p -> true);

        filterReferences.textProperty().addListener((observable, oldValue, newValue) -> filtered.setPredicate(refItem -> {
            if (newValue == null || newValue.isEmpty()) return true;
            String lowercaseFilter = newValue.toLowerCase(Locale.ROOT);
            return (refItem.token.toLowerCase(Locale.ROOT).contains(lowercaseFilter)
                    || refItem.getJoinedProps().toLowerCase(Locale.ROOT).contains(lowercaseFilter));
        }));

        // To re-enable column sorting
        SortedList<RefItem> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sorted);

        tokenCol.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().token())
        );

        propsCol.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getJoinedProps())
        );
    }


    /**
     * Call this when someone “search-activated” a token.
     */
    public void selectToken(String token) {
        // show the page (if you weren’t already on it)
        app.showReferencesView();

        // put the token text into the filter box (this will re-filter the table)
        filterReferences.setText(token);

        // then highlight it in the table
        tableView.getItems().stream()
                .filter(item -> item.token().equals(token))
                .findFirst()
                .ifPresent(item -> {
                    tableView.getSelectionModel().select(item);
                    tableView.scrollTo(item);
                });
    }

}
