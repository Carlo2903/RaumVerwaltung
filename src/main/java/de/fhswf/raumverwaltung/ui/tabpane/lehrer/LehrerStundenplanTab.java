package de.fhswf.raumverwaltung.ui.tabpane.lehrer;

import de.fhswf.raumverwaltung.db.entities.Lehrkraft;
import de.fhswf.raumverwaltung.db.entities.LehrerBenutzer;
import de.fhswf.raumverwaltung.service.BenutzerService;
import de.fhswf.raumverwaltung.ui.tabpane.MyTab;
import de.fhswf.raumverwaltung.ui.tabpane.Reloadable;
import de.fhswf.raumverwaltung.ui.tabpane.stundenplan.StundenplanRasterView;

public class LehrerStundenplanTab extends MyTab implements Reloadable {

    private final LehrerStundenplanViewModel viewModel;

    public LehrerStundenplanTab() {
        super("Mein Stundenplan");

        // Eigene Lehrkraft ermitteln
        Lehrkraft lehrkraft = null;
        if (BenutzerService.getInstance().getAktuellerBenutzer()
                instanceof LehrerBenutzer lehrerBenutzer) {
            lehrkraft = lehrerBenutzer.getLehrkraft();
        }

        this.viewModel = new LehrerStundenplanViewModel(lehrkraft);

        // readOnly = true – Lehrer kann nichts bearbeiten
        this.setContent(new StundenplanRasterView(viewModel, true));
    }

    @Override
    public void reload() {
        viewModel.laden();
    }
}