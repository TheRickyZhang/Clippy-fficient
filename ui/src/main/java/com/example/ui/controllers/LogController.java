package com.example.ui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.util.logging.*;

public class LogController extends BaseController {
    @FXML private TextArea logArea;
    @FXML private TextArea basicLogArea;

    @FXML public void initialize() {
        Handler logHandler = createFxLogHandler(logArea);
        Handler basicLogHandler = createFxLogHandler(basicLogArea);

        // Ensure that we only display logs from this app, not Java internals
        Logger appLog = Logger.getLogger("com.example");
        appLog.setLevel(Level.ALL);
        appLog.setUseParentHandlers(false);
        appLog.addHandler(logHandler);
    }

    protected static Handler createFxLogHandler(TextArea target) {
        Handler h = new Handler() {
            private final Formatter f = new SimpleFormatter();

            @Override public void publish(LogRecord record) {
                String s = f.format(record);
                Platform.runLater(() -> target.appendText(s + "\n"));
            }
            @Override public void flush() {}
            @Override public void close() throws SecurityException {}
        };
        h.setLevel(Level.ALL);
        return h;
    }
}
