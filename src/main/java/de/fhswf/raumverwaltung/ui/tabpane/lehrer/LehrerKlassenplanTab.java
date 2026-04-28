package de.fhswf.raumverwaltung.ui.tabpane.lehrer;

import de.fhswf.raumverwaltung.db.entities.Klasse;
import de.fhswf.raumverwaltung.db.entities.LehrerBenutzer;
import de.fhswf.raumverwaltung.service.BenutzerService;
import de.fhswf.raumverwaltung.ui.tabpane.MyTab;
import de.fhswf.raumverwaltung.ui.tabpane.Reloadable;
import de.fhswf.raumverwaltung.ui.tabpane.stundenplan.StundenplanRasterView;

public class LehrerKlassenplanTab extends LehrerStundenplanExport {

    private final LehrerStundenplanViewModel viewModel;


    public LehrerKlassenplanTab() {
        super("Meine Klasse");

        Klasse meineKlasse = BenutzerService.getInstance()
                .getKlasseDesKlassenlehrers();

        if (meineKlasse != null) {
            setText("Klasse " + meineKlasse.getBezeichnung());
        }

        LehrerBenutzer lb = (LehrerBenutzer)
                BenutzerService.getInstance().getAktuellerBenutzer();

        this.viewModel = new LehrerStundenplanViewModel(lb.getLehrkraft());

        if (meineKlasse != null) {
            viewModel.filterNachKlasse(meineKlasse);
        }

        buildContent(new StundenplanRasterView(viewModel, true));
    }

    @Override
    public void reload() { viewModel.laden(); }
}