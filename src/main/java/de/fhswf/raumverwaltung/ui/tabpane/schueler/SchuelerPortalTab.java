package de.fhswf.raumverwaltung.ui.tabpane.schueler;

import de.fhswf.raumverwaltung.ui.tabpane.MyTab;
import de.fhswf.raumverwaltung.ui.tabpane.Reloadable;

public class SchuelerPortalTab extends MyTab implements Reloadable {

    private final SchuelerPortalViewModel viewModel;

    public SchuelerPortalTab() {
        super("Mein Stundenplan");
        this.viewModel = new SchuelerPortalViewModel();
        this.setContent(new SchuelerPortalView(viewModel));
    }

    @Override
    public void reload() {
        viewModel.laden();
    }
}