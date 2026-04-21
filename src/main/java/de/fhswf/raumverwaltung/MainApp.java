package de.fhswf.raumverwaltung;

import atlantafx.base.theme.PrimerLight;
import de.fhswf.raumverwaltung.db.dao.SchuljahrDao;
import de.fhswf.raumverwaltung.db.dao.StundenplanDao;
import de.fhswf.raumverwaltung.db.entities.Schuljahr;
import de.fhswf.raumverwaltung.db.entities.Stundenplan;
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

    private void erstelleStandardSchuljahr() {
        SchuljahrDao schuljahrDao = new SchuljahrDao();

        // Nur anlegen falls noch kein aktives Schuljahr existiert
        if (schuljahrDao.findeAktives().isPresent()) return;

        Schuljahr schuljahr = Schuljahr.builder()
                .bezeichnung("2025/2026")
                .startdatum(java.time.LocalDate.of(2025, 8, 1))
                .enddatum(java.time.LocalDate.of(2026, 7, 31))
                .istAktiv(true)
                .build();
        schuljahrDao.persist(schuljahr);

        // Dazugehörigen Stundenplan anlegen
        StundenplanDao stundenplanDao = new StundenplanDao();
        Stundenplan stundenplan = new Stundenplan();
        stundenplan.setGueltigAb(java.time.LocalDate.of(2025, 8, 1));
        stundenplan.setSchuljahr(schuljahr);
        stundenplanDao.persist(stundenplan);

        System.out.println("Schuljahr 2025/2026 + Stundenplan angelegt.");
    }

    // Wird vom LoginViewModel nach erfolgreichem Login aufgerufen
    public static void showMainContent() {
        mainFrame.hideLogin();
    }
}