package de.fhswf.raumverwaltung.ui.tabpane;

import de.fhswf.raumverwaltung.db.entities.Benutzer;
import de.fhswf.raumverwaltung.service.BenutzerService;
import de.fhswf.raumverwaltung.ui.tabpane.fach.FachTab;
import de.fhswf.raumverwaltung.ui.tabpane.klasse.KlasseTab;
import de.fhswf.raumverwaltung.ui.tabpane.lehrkraft.LehrkraftTab;
import de.fhswf.raumverwaltung.ui.tabpane.raum.RaumTab;
import javafx.scene.control.TabPane;

public class MyTabPane extends TabPane {

    private static MyTabPane instance;

    private final RaumTab      raumTab;
    private final LehrkraftTab lehrkraftTab;
    private final FachTab      fachTab;
    private final KlasseTab    klasseTab;

    private MyTabPane() {
        this.raumTab      = new RaumTab();
        this.lehrkraftTab = new LehrkraftTab();
        this.fachTab      = new FachTab();
        this.klasseTab    = new KlasseTab();

        this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
    }

    public static MyTabPane getInstance() {
        if (instance == null) {
            instance = new MyTabPane();
        }
        return instance;
    }

    public void removeAll() {
        this.getTabs().clear();
    }

    public void addTabs() {
        this.removeAll();

        Benutzer benutzer = BenutzerService.getInstance().getAktuellerBenutzer();

        if (benutzer == null) return;

        switch (benutzer.getRolle()) {
            case ADMINISTRATOR -> {
                // Admin sieht alle Stammdaten-Tabs
                this.getTabs().addAll(
                        raumTab,
                        lehrkraftTab,
                        fachTab,
                        klasseTab
                        // AP 3: stundenplanTab, vertretungsTab
                );
            }
            case LEHRER -> {
                // AP 3: eigener Stundenplan + Vertretungsübersicht
                // this.getTabs().add(meineStundenTab);
            }
            case SCHUELER -> {
                // AP 3: nur Schülerportal
                // this.getTabs().add(schuelerPortalTab);
            }
        }
    }
}

