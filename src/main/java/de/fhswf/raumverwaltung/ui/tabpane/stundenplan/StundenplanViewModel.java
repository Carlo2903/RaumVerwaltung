package de.fhswf.raumverwaltung.ui.tabpane.stundenplan;

import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.service.KonfliktService;
import de.fhswf.raumverwaltung.db.exception.PlanungException;
import javafx.beans.property.*;
import javafx.collections.*;
import lombok.Getter;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class StundenplanViewModel implements Observer {

    private final StundenplanTableModel model;

    @Getter
    private final ObjectProperty<Map<Wochentag, Map<Integer, Stunde>>> gridProperty
            = new SimpleObjectProperty<>();

    @Getter
    private final ObjectProperty<ObservableList<Klasse>> klassenProperty
            = new SimpleObjectProperty<>();

    private final KonfliktService konfliktService = new KonfliktService();

    public StundenplanViewModel() {
        this.model = StundenplanTableModel.getInstance();
        this.model.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        gridProperty.set(model.getStundenGrid());
        klassenProperty.set(
                FXCollections.observableList(model.getAlleKlassen())
        );
    }

    public void laden()                        { model.laden(); }
    public void filterNachKlasse(Klasse k)     { model.filterNachKlasse(k); }

    public void stundeSetzen(Stunde stunde) throws PlanungException {
        // Konfliktprüfung vor dem Speichern
        konfliktService.validiereStunde(stunde);
        model.stundeSetzen(stunde);
    }

    public void stundeLoeschen(Stunde stunde)  { model.stundeLoeschen(stunde); }

    public Stundenplan getAktuellerPlan()      { return model.getAktuellerPlan(); }
}