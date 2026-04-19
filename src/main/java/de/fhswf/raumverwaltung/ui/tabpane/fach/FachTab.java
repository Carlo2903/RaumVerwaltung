package de.fhswf.raumverwaltung.ui.tabpane.fach;

import de.fhswf.raumverwaltung.ui.tabpane.MyTab;

public class FachTab extends MyTab {

    public FachTab() {
        super("Fächer");
        this.setContent(new FachTable());
    }
}