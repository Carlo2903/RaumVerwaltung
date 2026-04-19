package de.fhswf.raumverwaltung.ui.dialog.login;

import de.fhswf.raumverwaltung.db.entities.Benutzerrolle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginDialog extends Stage {

    private final LoginViewModel viewModel;

    // Formularfelder – package-private für ViewModel
    private final TextField     benutzernameField = new TextField();
    private final PasswordField passwortField      = new PasswordField();
    private final Label         fehlerLabel        = new Label();

    public LoginDialog(Stage owner) {
        this.viewModel = new LoginViewModel(this);

        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UNDECORATED);
        setResizable(false);

        Scene scene = new Scene(buildLayout(), 420, 520);
        setScene(scene);
    }

    // ---------------------------------------------------------------
    // Layout – entspricht eurem Mockup
    // ---------------------------------------------------------------

    private StackPane buildLayout() {
        // Grauer Hintergrund
        StackPane root = new StackPane(buildCard());
        root.setStyle("-fx-background-color: #f0f2f5;");
        return root;
    }

    private VBox buildCard() {
        VBox card = new VBox(16);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40, 48, 40, 48));
        card.setMaxWidth(360);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 16, 0, 0, 4);"
        );

        // Avatar-Kreis (wie im Mockup)
        Circle avatar = new Circle(48);
        avatar.setFill(Color.web("#3d5a80"));

        // Felder
        benutzernameField.setPromptText("Benutzername");
        benutzernameField.setPrefHeight(44);
        benutzernameField.setMaxWidth(Double.MAX_VALUE);
        benutzernameField.setStyle(
                "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-color: #d0d7de;" +
                        "-fx-padding: 8 12 8 12;"
        );

        passwortField.setPromptText("Passwort");
        passwortField.setPrefHeight(44);
        passwortField.setMaxWidth(Double.MAX_VALUE);
        passwortField.setStyle(benutzernameField.getStyle());

        // Fehlermeldung (anfangs unsichtbar)
        fehlerLabel.setStyle("-fx-text-fill: #cf222e; -fx-font-size: 13;");
        fehlerLabel.setVisible(false);
        fehlerLabel.setManaged(false);

        // Anmelden-Button
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

        // Enter-Taste auslöst auch Login
        passwortField.setOnAction(e -> handleLogin());

        card.getChildren().addAll(avatar, benutzernameField, passwortField, fehlerLabel, btnAnmelden);

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

    // Wird vom ViewModel aufgerufen bei LoginFailedEvent
    public void zeigeFehlermeldung() {
        fehlerLabel.setText("Benutzername oder Passwort ist falsch.");
        fehlerLabel.setVisible(true);
        fehlerLabel.setManaged(true);
        passwortField.clear();
        passwortField.requestFocus();
    }
}