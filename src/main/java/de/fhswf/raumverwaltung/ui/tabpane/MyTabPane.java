package de.fhswf.raumverwaltung.ui.tabpane;

import de.fhswf.raumverwaltung.db.entities.Benutzer;
import de.fhswf.raumverwaltung.service.BenutzerService;
import de.fhswf.raumverwaltung.ui.tabpane.fach.FachTab;
import de.fhswf.raumverwaltung.ui.tabpane.klasse.KlasseTab;
import de.fhswf.raumverwaltung.ui.tabpane.lehrkraft.LehrkraftTab;
import de.fhswf.raumverwaltung.ui.tabpane.raum.RaumTab;
import de.fhswf.raumverwaltung.ui.tabpane.schueler.SchuelerPortalTab;
import de.fhswf.raumverwaltung.ui.tabpane.stundenplan.StundenplanTab;
import de.fhswf.raumverwaltung.ui.tabpane.vertretung.VertretungTab;
import javafx.scene.control.TabPane;

public class MyTabPane extends TabPane {

    private static MyTabPane instance;

    private final RaumTab          raumTab          = new RaumTab();
    private final LehrkraftTab     lehrkraftTab     = new LehrkraftTab();
    private final FachTab          fachTab          = new FachTab();
    private final KlasseTab        klasseTab        = new KlasseTab();
    private final StundenplanTab   stundenplanTab   = new StundenplanTab();
    private final VertretungTab    vertretungTab    = new VertretungTab();
    private final SchuelerPortalTab schuelerTab     = new SchuelerPortalTab();

    private MyTabPane() {
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
            case ADMINISTRATOR -> this.getTabs().addAll(
                    raumTab,
                    lehrkraftTab,
                    fachTab,
                    klasseTab,
                    stundenplanTab,
                    vertretungTab
            );
            case LEHRER -> this.getTabs().addAll(
                    stundenplanTab,
                    vertretungTab
            );
            case SCHUELER -> this.getTabs().addAll(
                    schuelerTab
            );
        }
    }
}