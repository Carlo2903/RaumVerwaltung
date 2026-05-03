package de.fhswf.raumverwaltung.ui.dialog.login;

import de.fhswf.raumverwaltung.db.entities.Benutzerrolle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class LoginView extends StackPane {

    private final LoginViewModel    viewModel;
    private final TextField         benutzernameField = new TextField();
    private final PasswordField     passwortField     = new PasswordField();
    private final Label             fehlerLabel       = new Label();

    public LoginView() {
        this.viewModel = new LoginViewModel();
        this.setStyle("-fx-background-color: #f0f2f5;");
        this.getChildren().add(buildCard());

        // Reaktiv auf Fehlermeldungen aus dem ViewModel reagieren (MVVM-konform)
        viewModel.getFehlerProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isBlank()) {
                fehlerLabel.setText(newVal);
                fehlerLabel.setVisible(true);
                fehlerLabel.setManaged(true);
                passwortField.clear();
                passwortField.requestFocus();
            } else {
                fehlerLabel.setVisible(false);
                fehlerLabel.setManaged(false);
            }
        });
    }

    // ---------------------------------------------------------------
    // Layout
    // ---------------------------------------------------------------

    private VBox buildCard() {
        VBox card = new VBox(16);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(48));
        card.setMaxWidth(360);
        card.setMaxHeight(420);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 20, 0, 0, 4);"
        );

        // Avatar
        Circle avatar = new Circle(48);
        avatar.setFill(Color.web("#3d5a80"));

        // Felder
        benutzernameField.setPromptText("Benutzername");
        benutzernameField.setPrefHeight(44);
        benutzernameField.setMaxWidth(Double.MAX_VALUE);

        passwortField.setPromptText("Passwort");
        passwortField.setPrefHeight(44);
        passwortField.setMaxWidth(Double.MAX_VALUE);
        passwortField.setOnAction(e -> handleLogin());

        // Fehlermeldung
        fehlerLabel.setStyle("-fx-text-fill: #cf222e; -fx-font-size: 13;");
        fehlerLabel.setVisible(false);
        fehlerLabel.setManaged(false);

        // Button
        Button btnAnmelden = new Button("Anmelden");
        btnAnmelden.setMaxWidth(Double.MAX_VALUE);
        btnAnmelden.setPrefHeight(44);
        btnAnmelden.setStyle(
                "-fx-background-color: #3d5a80;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 22;"
        );
        btnAnmelden.setOnAction(e -> handleLogin());

        card.getChildren().addAll(
                avatar,
                benutzernameField,
                passwortField,
                fehlerLabel,
                btnAnmelden
        );

        return card;
    }

    // ---------------------------------------------------------------
    // Logik
    // ---------------------------------------------------------------

    private void handleLogin() {
        fehlerLabel.setVisible(false);
        fehlerLabel.setManaged(false);
        viewModel.doLogin(
                benutzernameField.getText().trim(),
                passwortField.getText()
        );
    }
}