// src/main/java/com/example/ui/controllers/LogController.java
package com.example.ui.controllers;

import com.example.core.logging.LogService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;

import java.util.function.Consumer;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class LogController extends BaseController {
    private static final int MAX_LOG_LENGTH = 5000;
    @FXML private SplitPane root;

    @FXML private TextArea logArea;
    @FXML private TextArea basicLogArea;

    // these hold each controller instance’s subscription callbacks
    private Consumer<LogRecord> detailedSubscriber;
    private Consumer<LogRecord> simpleSubscriber;

    @FXML
    public void initialize() {
        // build the callbacks
        detailedSubscriber = record -> {
            String formatted = new SimpleFormatter().format(record);
            Platform.runLater(() -> appendAndPrune(logArea, formatted));
        };
        simpleSubscriber = record -> {
            String msg = record.getMessage() + "\n";
            Platform.runLater(() -> appendAndPrune(basicLogArea, msg));
        };

        // subscribe to the global log streams
        LogService.addDetailedListener(detailedSubscriber);
        LogService.addSimpleListener(simpleSubscriber);

        root.parentProperty().addListener((observable, oldParent, newParent) -> {
            if(newParent == null) cleanup();
        });
    }


    /**
     * Call this in your AppController *before* unloading/removing this view
     * to avoid leaking subscriptions.
     */
    public void cleanup() {
        LogService.removeDetailedListener(detailedSubscriber);
        LogService.removeSimpleListener(simpleSubscriber);
    }

    private void appendAndPrune(TextArea area, String text) {
        area.appendText(text);
        int over = area.getLength() - MAX_LOG_LENGTH;
        if (over > 0) {
            area.deleteText(0, over);
        }
    }
}
