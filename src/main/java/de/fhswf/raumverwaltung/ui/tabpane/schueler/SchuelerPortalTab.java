package de.fhswf.raumverwaltung.ui.tabpane.schueler;

import de.fhswf.raumverwaltung.ui.tabpane.MyTab;

public class SchuelerPortalTab extends MyTab {

    public SchuelerPortalTab() {
        super("Mein Stundenplan");
        this.setContent(new SchuelerPortalView());
    }
}