package de.fhswf.raumverwaltung.ui.tabpane.klasse;

import de.fhswf.raumverwaltung.ui.tabpane.MyTab;

public class KlasseTab extends MyTab {

    public KlasseTab() {
        super("Klassen");
        this.setContent(new KlasseTable());
    }
}