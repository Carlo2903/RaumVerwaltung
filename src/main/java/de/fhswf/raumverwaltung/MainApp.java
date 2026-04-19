package de.fhswf.raumverwaltung;

import de.fhswf.raumverwaltung.ui.MainFrame;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import atlantafx.base.theme.PrimerLight;  // NEU

public class MainApp extends Application {

    public static Stage primaryStage;     // NEU – für spätere Notifications

    @Override
    public void start(Stage stage) {
        // NEU: Theme als allererstes setzen, vor allem anderen
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        MainFrame mainFrame = new MainFrame();
        Scene scene = new Scene(mainFrame, 1200, 800);

        primaryStage = stage;             // NEU
        stage.setTitle("Schul-Planer Pro 2026");
        stage.setScene(scene);
        stage.show();
    }
}