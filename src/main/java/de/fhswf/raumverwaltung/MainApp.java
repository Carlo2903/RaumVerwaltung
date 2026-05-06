package de.fhswf.raumverwaltung;

import atlantafx.base.theme.PrimerLight;
import de.fhswf.raumverwaltung.db.dao.*;
import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.db.exception.PlanungException;
import de.fhswf.raumverwaltung.service.AutoRefreshService;
import de.fhswf.raumverwaltung.service.BenutzerService;
import de.fhswf.raumverwaltung.ui.MainFrame;
import de.fhswf.raumverwaltung.ui.tabpane.MyTabPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class MainApp extends Application {


    public static Stage primaryStage;
    private static MainFrame mainFrame;

    @Override
    public void start(Stage stage) throws PlanungException {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        primaryStage = stage;
        mainFrame    = new MainFrame();

        erstelleAdmin();
        erstelleStandardSchuljahr();
        erstelleZeitslots();
        erstelleBenutzer();

        Scene scene = new Scene(mainFrame, 1200, 800);

        stage.setMinWidth(900);
        stage.setMinHeight(600);


        stage.setTitle("Schul-Planer Pro 2026");
        try {
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/logo2.png")));
        } catch (Exception e) {
            System.err.println("Konnte App-Icon nicht laden.");
        }
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

    private void erstelleZeitslots() {
        ZeitslotDao dao = new ZeitslotDao();
        if (!dao.findAll().isEmpty()) return;

        // 6 Stunden pro Tag, Mo–Fr
        String[][] zeiten = {
                {"08:00", "08:45"},
                {"08:45", "09:30"},
                {"09:45", "10:30"},
                {"10:30", "11:15"},
                {"11:30", "12:15"},
                {"12:15", "13:00"}
        };

        Wochentag[] tage = {
                Wochentag.MONTAG, Wochentag.DIENSTAG, Wochentag.MITTWOCH,
                Wochentag.DONNERSTAG, Wochentag.FREITAG
        };

        for (Wochentag tag : tage) {
            for (int i = 0; i < zeiten.length; i++) {
                Zeitslot z = new Zeitslot();
                z.setWochentag(tag);
                z.setStundenNummer(i + 1);
                z.setStartzeit(java.time.LocalTime.parse(zeiten[i][0]));
                z.setEndzeit(java.time.LocalTime.parse(zeiten[i][1]));
                dao.persist(z);
            }
        }
        System.out.println("30 Zeitslots angelegt (6 pro Tag, Mo–Fr).");
    }

    private void erstelleBenutzer() {
        try {
            BenutzerService.getInstance().erstelleBenutzerFallsNichtVorhanden(
                    new LehrkraftDao().findAll(),
                    new KlasseDao().findAll()
            );
        } catch (PlanungException e) {
            System.err.println("Fehler beim Anlegen der Benutzer: " + e.getMessage());
        }
    }


    private void erstelleAdmin() {
        try {
            BenutzerService.getInstance().erstelleAdminFallsNichtVorhanden();
        } catch (PlanungException e) {
            System.err.println("Fehler: " + e.getMessage());
        }
    }



    // Wird vom LoginViewModel nach erfolgreichem Login aufgerufen
    public static void showMainContent() {
        mainFrame.hideLogin();
        AutoRefreshService.getInstance().starten();
    }
}