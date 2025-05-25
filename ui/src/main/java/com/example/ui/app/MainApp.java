package com.example.ui.app;

import com.example.agent.GlobalHookService;
import com.example.core.ShortcutEngine;
import com.example.core.context.ApplicationContext;
import com.example.core.utils.AppAction;
import com.example.core.utils.Suggestion;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MainApp extends Application {
    private ApplicationContext context;
    private ShortcutEngine engine;
    private GlobalHookService hookService;

    @Override
    public void start(Stage stage) throws Exception {
        context = new ApplicationContext(List.of());
        engine = new ShortcutEngine(initUIListeners());
        hookService = new GlobalHookService(engine::onEvent);

        URL fxmlUrl = getClass().getResource("/com/example/ui/app/AppView.fxml");
        if (fxmlUrl == null) {
            throw new IllegalStateException("Couldn’t find AppView.fxml on the classpath!");
        }
        FXMLLoader loader = new FXMLLoader(fxmlUrl);


        loader.setControllerFactory(type -> {
            if (type == AppController.class) {
                return new AppController(engine);
            }
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        Parent root;
        try {
            root = loader.load();
            System.out.println("FXML loaded, controller = " + loader.getController());
        } catch (Exception e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            throw e;
        }

        stage.setScene(new Scene(root, 700, 500));
        stage.setTitle("Shortcut Reinforcement Agent");
        stage.show();
    }

    @Override
    public void stop() {
        hookService.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Map<AppAction, Consumer<Suggestion>> initUIListeners() {
        return Map.of(
                AppAction.OPEN_POPUP,
                s -> Platform.runLater(() ->
                        new Alert(Alert.AlertType.INFORMATION, s.getTip())
                                .showAndWait()
                ),
                AppAction.MAKE_SOUND,
                s -> System.out.println("Made sound" + s.getTip())
        );
    }

}
