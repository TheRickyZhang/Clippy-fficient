package com.example.ui.controllers;

import com.example.ui.api.ApiClient;
import com.example.ui.app.Page;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.http.HttpResponse;
import java.util.Map;

public class SigninController extends BaseController {
    @FXML
    private Label titleLabel;
    @FXML
    private VBox extraFields;
    @FXML
    private Button submitButton, toggleButton;
    @FXML
    private TextField emailField, confirmEmailField, nameField;
    @FXML
    private PasswordField passwordField;

    private final SimpleBooleanProperty showingSignup = new SimpleBooleanProperty(false);

    private final ApiClient api = new ApiClient();

    @FXML
    private void initialize() {
        extraFields.managedProperty().bind(showingSignup);
        extraFields.visibleProperty().bind(showingSignup);

        titleLabel.textProperty().bind(
                Bindings.when(showingSignup)
                        .then("Sign Up")
                        .otherwise("Sign In")
        );
        submitButton.textProperty().bind(
                Bindings.when(showingSignup)
                        .then("Register")
                        .otherwise("Login")
        );
        toggleButton.textProperty().bind(
                Bindings.when(showingSignup)
                        .then("Back to Login")
                        .otherwise("Create Account")
        );
    }

    @FXML
    private void onToggleMode() {
        showingSignup.set(!showingSignup.get());
    }

    @FXML
    private void onSubmit() {
        if (showingSignup.get()) {
            doSignup();
        } else {
            doLogin();
        }
    }

    private void doSignup() {
        String email = emailField.getText();
        String confirmEmail = confirmEmailField.getText();
        String password = passwordField.getText();
        System.out.println("trying to sign up with " + email + " " + password);

        String name = nameField.getText();

        if (!email.equals(confirmEmail)) {
            showAlert("Emails don’t match");
            return;
        }
        var payload = Map.of("name", name, "email", email, "password", password);
        try {
            api.postJson("/users", payload)
                .whenComplete((res, ex) -> {
                    if (ex != null) {
                        ex.printStackTrace();    // shows network/JSON errors
                        Platform.runLater(() -> showAlert("HTTP error: " + ex.getMessage()));
                    } else {
                        handleResponse(res);
                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(e.getMessage());
        }
    }

    private void doLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();
        System.out.println("trying to login up with " + email + " " + password);
        try {
            api.postJson("/users/login", Map.of(
                    "email", email,
                    "password", password
            )).thenAccept(this::handleResponse);
        } catch (Exception e) {
            showAlert(e.getMessage());
        }
    }

    // Private
    private void handleResponse(HttpResponse<String> res) {
        System.out.println("HTTP " + res.statusCode() + " → '" + res.body() + "'");
        Platform.runLater(() -> {
            if (res.statusCode() == 200) {
                app.showPage(Page.SUGGESTIONS);
            } else {
                showAlert((showingSignup.get() ? "Signup" : "Login") + " failed" +
                        " (status " + res.statusCode() + " " + res.body() + ")");
            }
        });
    }


    private void showAlert(String msg) {
        System.out.println("Showing alert" + msg);
        var a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.initOwner(submitButton.getScene().getWindow());
        a.showAndWait();
    }


}
