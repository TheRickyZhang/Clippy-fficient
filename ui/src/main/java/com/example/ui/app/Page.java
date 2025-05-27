package com.example.ui.app;

import com.example.ui.controllers.*;

import java.util.function.BiConsumer;

public enum Page {
    SUGGESTIONS(
            "HomeView.fxml",
            HomeController.class,
            (app, ctrl) -> app.homeController = (HomeController) ctrl
    ),
    REFERENCES(
            "ReferencesView.fxml",
            ReferencesController.class,
            (app, ctrl) -> app.referencesController = (ReferencesController) ctrl
    ),
    LOG(
            "LogView.fxml",
            LogController.class,
            (app, ctrl) -> app.logController = (LogController) ctrl
    ),
    SIGNIN(
            "SigninView.fxml",
            SigninController.class,
            (app, ctrl) -> app.signinController = (SigninController) ctrl
    );

    private final String fxmlPath;
    private final Class<? extends BaseController> ctrlType;
    private final BiConsumer<AppController,BaseController> setter;

    Page(String fxml, Class<? extends BaseController> type,
         BiConsumer<AppController,BaseController> setter) {
        this.fxmlPath  = fxml;
        this.ctrlType  = type;
        this.setter    = setter;
    }

    public String getFxmlPath() { return "/com/example/ui/controllers/" + fxmlPath; }
    public Class<? extends BaseController> getCtrlType() { return ctrlType; }
    public void install(AppController app, BaseController ctrl) { setter.accept(app, ctrl); }
}
