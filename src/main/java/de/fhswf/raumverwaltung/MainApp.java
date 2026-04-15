package de.fhswf.raumverwaltung;

import de.fhswf.raumverwaltung.ui.MainFrame;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        try {
            // Wir nutzen das MainFrame (mit MenuBar und TabPane) als Root
            MainFrame mainFrame = new MainFrame();

            Scene scene = new Scene(mainFrame, 1200, 800);
            stage.setTitle("Schul-Planer Pro 2026");
            stage.setScene(scene);
            stage.show();

            System.out.println("UI erfolgreich gestartet!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}