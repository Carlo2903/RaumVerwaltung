package de.fhswf.raumverwaltung.ui.tabpane.lehrer;

import de.fhswf.raumverwaltung.MainApp;
import de.fhswf.raumverwaltung.db.entities.Lehrkraft;
import de.fhswf.raumverwaltung.db.entities.LehrerBenutzer;
import de.fhswf.raumverwaltung.service.BenutzerService;
import de.fhswf.raumverwaltung.ui.tabpane.MyTab;
import de.fhswf.raumverwaltung.ui.tabpane.Reloadable;
import de.fhswf.raumverwaltung.ui.tabpane.stundenplan.StundenplanRasterView;
import javafx.geometry.Insets;
import javafx.print.PageOrientation;
import javafx.print.Printer;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class LehrerStundenplanTab extends LehrerStundenplanExport {

    private final LehrerStundenplanViewModel viewModel;


    public LehrerStundenplanTab() {
        super("Mein Stundenplan");

        Lehrkraft lehrkraft = null;
        if (BenutzerService.getInstance().getAktuellerBenutzer()
                instanceof LehrerBenutzer lb) {
            lehrkraft = lb.getLehrkraft();
        }

        this.viewModel = new LehrerStundenplanViewModel(lehrkraft);
        buildContent(new StundenplanRasterView(viewModel, true));
    }

    @Override
    public void reload() { viewModel.laden();
    }
}



