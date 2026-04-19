package de.fhswf.raumverwaltung.ui.tabpane;

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
        this.getTabs().addAll(raumTab, lehrkraftTab, fachTab, klasseTab);
    }
}