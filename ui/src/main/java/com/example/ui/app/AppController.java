package com.example.ui.app;

import com.example.core.ShortcutEngine;
import com.example.core.search.SearchEntry;
import com.example.core.search.SearchService;
import com.example.core.tokens.TokenRegistry;
import com.example.ui.components.HeaderController;
import com.example.ui.controllers.BaseController;
import com.example.ui.controllers.LogController;
import com.example.ui.controllers.ReferencesController;
import com.example.ui.controllers.HomeController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class AppController {
    @FXML private HeaderController header;
    @FXML private BorderPane       rootView;

    private final ShortcutEngine engine;
    private final KeyCombination logKeybind       = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN);
    private final KeyCombination referencesKeybind= new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);

    // Stores each page’s scene graph so we don’t reload FXML on every switch
    private final Map<String, Parent> pageViews = new HashMap<>();

    // Will be set during preloadAllPages()
    private HomeController suggestionsController;
    private ReferencesController  referencesController;
    private LogController         logController;

    /** One‐line description of each page: name, its FXML, its controller type, and how to stash it. */
    private static final List<PageDef<? extends BaseController>> PAGES = List.of(
            new PageDef<>("Home",
                    "/com/example/ui/controllers/HomeView.fxml",
                    HomeController.class,
                    (app, ctrl) -> app.suggestionsController = ctrl
            ),
            new PageDef<>("References",
                    "/com/example/ui/controllers/ReferencesView.fxml",
                    ReferencesController.class,
                    (app, ctrl) -> app.referencesController = ctrl
            ),
            new PageDef<>("Log",
                    "/com/example/ui/controllers/LogView.fxml",
                    LogController.class,
                    (app, ctrl) -> app.logController = ctrl
            )
    );

    public AppController(ShortcutEngine engine) {
        this.engine = engine;
    }

    /** Called by FXMLLoader once @FXML‐injection is done. */
    @FXML
    private void initialize() {
        // Let the header buttons / search icon know how to talk back to us
        header.setAppController(this);

        //Load and cache ALL pages (even if user never visits them)
        PAGES.forEach(this::preloadPage);

        initSearch();
        showMainView();
    }

    /** Loads the FXML, grabs its controller, calls setAppController(...), stashes view+controller. */
    private <T extends BaseController> void preloadPage(PageDef<T> page) {
        try {
            URL url = getClass().getResource(page.fxmlPath());
            if (url == null) throw new IllegalStateException("Cannot find FXML: " + page.fxmlPath());

            FXMLLoader loader = new FXMLLoader(url);
            Parent view = loader.load();

            T ctrl = page.controllerClass().cast(loader.getController());
            ctrl.setAppController(this);
            page.setter().accept(this, ctrl);

            // cache the scene graph
            pageViews.put(page.name(), view);
        } catch (IOException ex) {
            Platform.runLater(() ->
                    new Alert(Alert.AlertType.ERROR,
                            "Failed to load “" + page.name() + "” view:\n" + ex.getMessage())
                            .showAndWait()
            );
        }
    }

    // Set up search providers
    // TODO: Set up for other pages
    private void initSearch() {
        SearchService svc = SearchService.get();
        svc.registerProvider("References", () ->
                TokenRegistry.getTokenMapValues().stream()
                        .map(e -> new SearchEntry(
                                "References",
                                e.getKey(),
                                () -> {
                                    showReferencesView();
                                    // Now that we know referencesController is non-null, select the token
                                    referencesController.selectToken(e.getKey());
                                }))
                        .toList()
        );
    }

    public void showMainView()       { rootView.setCenter(pageViews.get("Suggestions")); }
    public void showReferencesView() { rootView.setCenter(pageViews.get("References")); }
    public void showLogView()        { rootView.setCenter(pageViews.get("Log")); }

    public ShortcutEngine getEngine() { return engine; }

    // Bundles the name FXML path controller class field‐setter into one record.
    private record PageDef<T extends BaseController>(
            String name,
            String fxmlPath,
            Class<T> controllerClass,
            BiConsumer<AppController, T> setter
    ) {}
}
