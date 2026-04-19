package de.fhswf.raumverwaltung;

import atlantafx.base.theme.PrimerLight;
import de.fhswf.raumverwaltung.ui.MainFrame;
import de.fhswf.raumverwaltung.ui.tabpane.MyTabPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {


    public static Stage     primaryStage;
    private static MainFrame mainFrame;

    @Override
    public void start(Stage stage) {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        primaryStage = stage;
        mainFrame    = new MainFrame();

        Scene scene = new Scene(mainFrame, 1200, 800);
        stage.setTitle("Schul-Planer Pro 2026");
        stage.setScene(scene);
        stage.show();
    }

    // Wird vom LoginViewModel nach erfolgreichem Login aufgerufen
    public static void showMainContent() {
        mainFrame.hideLogin();
    }
}