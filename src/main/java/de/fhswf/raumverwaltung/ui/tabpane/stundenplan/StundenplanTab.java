package de.fhswf.raumverwaltung.ui.tabpane.stundenplan;

import de.fhswf.raumverwaltung.ui.tabpane.MyTab;
import de.fhswf.raumverwaltung.ui.tabpane.Reloadable;

public class StundenplanTab extends MyTab implements Reloadable {

    private final StundenplanViewModel viewModel;

    public StundenplanTab() {
        super("Stundenplan");
        this.viewModel = new StundenplanViewModel();
        this.setContent(new StundenplanRasterView(viewModel, false));
    }

    @Override
    public void reload() {
        viewModel.laden();
    }
}