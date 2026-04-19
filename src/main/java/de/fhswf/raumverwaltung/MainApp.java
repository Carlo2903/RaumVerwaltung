package de.fhswf.raumverwaltung;

import atlantafx.base.theme.PrimerLight;
import de.fhswf.raumverwaltung.ui.MainFrame;
import de.fhswf.raumverwaltung.ui.dialog.login.LoginDialog;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        primaryStage = stage;

        MainFrame mainFrame = new MainFrame();
        Scene scene = new Scene(mainFrame, 1200, 800);

        stage.setTitle("Schul-Planer Pro 2026");
        stage.setScene(scene);
        stage.show();

        // Login-Dialog direkt beim Start anzeigen
        LoginDialog loginDialog = new LoginDialog(stage);
        loginDialog.showAndWait();
    }
}