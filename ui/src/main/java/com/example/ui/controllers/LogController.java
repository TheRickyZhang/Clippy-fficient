package com.example.ui.controllers;

import com.example.core.logging.LogService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.util.logging.*;

public class LogController extends BaseController {
    @FXML private TextArea logArea;
    @FXML private TextArea basicLogArea;

    @FXML public void initialize() {
        Handler logHandler = createFxLogHandler(logArea, new SimpleFormatter());
        Handler basicLogHandler = createFxLogHandler(basicLogArea, new Formatter() {
            @Override public String format(LogRecord r) { return r.getMessage(); }
        });

        // Ensure that we only display logs from this app, not Java internals
        LogService.detailed().addHandler(logHandler);
        LogService.simple().addHandler(basicLogHandler);
    }

    protected static Handler createFxLogHandler(TextArea target, Formatter formatter) {
        Handler h = new Handler() {
            @Override public void publish(LogRecord record) {
                String s = formatter.format(record);
                Platform.runLater(() -> target.appendText(s + "\n"));
            }
            @Override public void flush() {}
            @Override public void close() throws SecurityException {}
        };
        h.setLevel(Level.ALL);
        return h;
    }
}
