package de.fhswf.raumverwaltung.ui.tabpane.lehrkraft;

import de.fhswf.raumverwaltung.ui.tabpane.MyTab;

public class LehrkraftTab extends MyTab {

    public LehrkraftTab() {
        super("Lehrkräfte");
        this.setContent(new LehrkraftTable());
    }
}