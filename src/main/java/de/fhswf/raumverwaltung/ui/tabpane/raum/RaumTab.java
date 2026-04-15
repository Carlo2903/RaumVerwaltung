package de.fhswf.raumverwaltung.ui.tabpane.raum;

import de.fhswf.raumverwaltung.ui.tabpane.MyTab;

public class RaumTab extends MyTab {
    public RaumTab() {
        super("Raumverwaltung");
        this.setContent(new RaumTable());
    }
}