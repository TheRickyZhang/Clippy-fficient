package com.example.ui.app;

import com.example.agent.listener.GlobalHookService;
import com.example.core.ShortcutRegistry;
import com.example.core.SuggestionRegistry;
import com.example.core.sequence.SequencePattern;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class AppController {
    private final ShortcutRegistry engine = new ShortcutRegistry();
    private final UIController ui;

    public AppController(UIController ui) {
        this.ui = ui;
        this.ui.setAppController(this);
        System.out.println("HERE");
    }

    public void initialize() {
        SuggestionRegistry.initDefaults();
        SuggestionRegistry.all().forEach((p, h) ->
                engine.addPattern(p, () -> ui.appendLog("Hint: " + h))
        );
        try {
            GlobalHookService.start(engine::onEvent);
        } catch (RuntimeException ex) {
            System.err.println("Cannot start GlobalHookService");
            ex.printStackTrace();
            Platform.runLater(() -> {
                new Alert(Alert.AlertType.ERROR,
                        "Failed to start global hook: " + ex.getCause().getMessage())
                        .showAndWait();
                Platform.exit();
            });
        }
    }

    public void showAddDialog(TableView<Suggestion> table) {
        TextInputDialog patDlg = new TextInputDialog();
        patDlg.setTitle("New Shortcut Pattern");
        patDlg.setHeaderText("Enter your pattern (e.g. scroll→click→type):");
        patDlg.setContentText("Pattern:");
        Optional<String> pat = patDlg.showAndWait();

        pat.ifPresent(desc -> {
            // 2) Prompt for the hint text
            TextInputDialog hintDlg = new TextInputDialog();
            hintDlg.setTitle("New Hint Text");
            hintDlg.setHeaderText("Enter the hint to display when that pattern is seen:");
            hintDlg.setContentText("Hint:");
            Optional<String> hint = hintDlg.showAndWait();

            hint.ifPresent(text -> {
                // 3) Build a SequencePattern from the user’s string
                SequencePattern custom = SequencePattern.literal(desc);

                // 4) Update your global data & your engine
                SuggestionRegistry.addCustom(custom, text);
                engine.addPattern(custom, () -> ui.appendLog("Hint: " + text));

                // 5) Update the table in the UI
                table.getItems().add(new Suggestion(custom, text));
            });
        });
    }

    public void removePattern(SequencePattern p) {
        SuggestionRegistry.remove(p);
        engine.removePattern(p);
    }
}