package com.example.ui.controllers;

import com.example.core.sequence.InputSequence;
import com.example.ui.app.Suggestion;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;

public class SuggestionsController extends BaseController {
    @FXML private TableView<Suggestion> suggestionTable;
    @FXML private TableColumn<Suggestion,String> patternCol;
    @FXML private TableColumn<Suggestion,String> tipCol;
    @FXML private TableColumn<Suggestion,Void> actionCol;

    @FXML
    public void initialize() {
        suggestionTable.setItems(FXCollections.observableArrayList());
        suggestionTable.setEditable(true);

        patternCol.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getPattern().toString())
        );
        tipCol.setCellValueFactory(c ->
                c.getValue().tipProperty()
        );
        tipCol.setOnEditCommit(e ->
                e.getRowValue().setTip(e.getNewValue())
        );

        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Delete");
            {
                btn.setOnAction(evt -> {
                    Suggestion s = getTableView().getItems().get(getIndex());
                    InputSequence seq = s.getPattern();
                    engine.removeAction(seq);
                    engine.removeTip(seq);
                    getTableView().getItems().remove(s);
                });
            }
            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }


    @FXML
    private void onAddHintClicked() {
        TextInputDialog patDlg = new TextInputDialog();
        patDlg.setTitle("New Shortcut Pattern");
        patDlg.setHeaderText("Enter pattern (e.g. scroll>click>type):");
        Optional<String> pat = patDlg.showAndWait();
        pat.ifPresent(desc -> {
            TextInputDialog hintDlg = new TextInputDialog();
            hintDlg.setTitle("New Tip Text");
            hintDlg.setHeaderText("Enter the tip to display:");
            Optional<String> tip = hintDlg.showAndWait();
            tip.ifPresent(text -> {
                InputSequence seq = new InputSequence(desc);
                engine.addTip(seq, text);
                suggestionTable.getItems().add(new Suggestion(seq, text));
            });
        });
    }

}
