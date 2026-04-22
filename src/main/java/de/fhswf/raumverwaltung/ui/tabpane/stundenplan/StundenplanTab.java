package de.fhswf.raumverwaltung.ui.tabpane.stundenplan;

import de.fhswf.raumverwaltung.ui.tabpane.MyTab;
import de.fhswf.raumverwaltung.ui.tabpane.Reloadable;

public class StundenplanTab extends MyTab implements Reloadable {

    public StundenplanTab() {
        super("Stundenplan");
        StundenplanViewModel viewModel = new StundenplanViewModel();
        this.setContent(new StundenplanRasterView(viewModel));
    }

    @Override
    public void reload() {

    }
}