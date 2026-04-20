package de.fhswf.raumverwaltung.ui.tabpane.stundenplan;

import de.fhswf.raumverwaltung.ui.tabpane.MyTab;

public class StundenplanTab extends MyTab {

    public StundenplanTab() {
        super("Stundenplan");
        StundenplanViewModel viewModel = new StundenplanViewModel();
        this.setContent(new StundenplanRasterView(viewModel));
    }
}