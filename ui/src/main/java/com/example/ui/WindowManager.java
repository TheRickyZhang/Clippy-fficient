package com.example.ui;

import com.example.core.logging.LogService;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Logger;

public class WindowManager {
    static WindowManager instance = new WindowManager();
    final int MAX_WINDOWS = 5; // Number greater than the base window
    Deque<Stage> openStages = new ArrayDeque<>();
    Logger log = LogService.get().forClass(WindowManager.class);

    public static WindowManager get() {
        return instance;
    }

    private WindowManager() {
    }

    public void addWindow(Stage s) {
        while (openStages.size() >= MAX_WINDOWS) {
            log.warning("Too many windows, closing some automatically");
            Stage old = openStages.removeFirst();
            old.close();
        }
        openStages.addLast(s);
        // Delete windows from the deque when they're closed
        s.setOnCloseRequest(e -> openStages.remove(s));
        s.show();
    }

    public void showAlert(Alert alert) {
        while (openStages.size() >= MAX_WINDOWS) {
            log.warning("Too many windows, closing some automatically");
            Stage old = openStages.removeFirst();
            old.close();
        }
        alert.show();
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        openStages.addLast(alertStage);
        alertStage.setOnCloseRequest(e -> openStages.remove(alertStage));
    }
}
