package com.example.ui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.util.logging.*;

public class LogController extends BaseController {
    @FXML private TextArea logArea;

    @FXML public void initialize() {
        Handler fxHandler = new Handler () {
            private final Formatter f = new SimpleFormatter();
            @Override
            public void publish(LogRecord record) {
                String line = f.format(record);
                Platform.runLater(() -> logArea.appendText(line + "\n"));
            }
            @Override
            public void flush() {}
            @Override public void close() throws SecurityException {}
        };
        fxHandler.setLevel(Level.ALL);
        Logger.getLogger("").addHandler(fxHandler);
    }
}
