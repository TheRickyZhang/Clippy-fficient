package com.example.ui.search;

import com.example.core.search.SearchEntry;
import com.example.core.search.SearchService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class SearchDialog extends Stage {
    @FXML TextField    tf;
    @FXML ListView<SearchEntry> results;

    public SearchDialog() {
        FXMLLoader f = new FXMLLoader(getClass().getResource("SearchDialog.fxml"));
        f.setController(this);
        try { setScene(new Scene(f.load())); }
        catch(IOException e){ throw new RuntimeException(e); }
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Global Search");

        // as you type, update the list
        tf.textProperty().addListener((o,p,newQ) -> {
            var hits = SearchService.get().search(newQ);
            results.setItems(FXCollections.observableList(hits));
        });

        // on double-click, activate and close
        results.setOnMouseClicked(ev -> {
            if (ev.getClickCount()==2) {
                SearchEntry sel = results.getSelectionModel().getSelectedItem();
                if (sel!=null) {
                    sel.action().run();
                    close();
                }
            }
        });
    }

    public void open() {
        tf.setText("");
        results.getItems().clear();
        showAndWait();
    }
}
