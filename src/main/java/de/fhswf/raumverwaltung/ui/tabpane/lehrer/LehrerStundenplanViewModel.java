package de.fhswf.raumverwaltung.ui.tabpane.lehrer;

import de.fhswf.raumverwaltung.db.dao.*;
import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.ui.tabpane.stundenplan.StundenplanViewModelInterface;
import de.fhswf.raumverwaltung.ui.util.VertretungUtil;
import javafx.beans.property.*;
import javafx.collections.*;
import lombok.Getter;

import java.util.*;

public class LehrerStundenplanViewModel implements Observer, StundenplanViewModelInterface  {

    // KEIN Singleton – eigene Instanz pro Lehrer-Session
    private final LehrerStundenplanModel model;

    @Getter
    private final ObjectProperty<Map<Wochentag, Map<Integer, Stunde>>> gridProperty
            = new SimpleObjectProperty<>();

    @Getter
    private final ObservableList<Stunde> stundenListe
            = FXCollections.observableArrayList();

    @Getter
    private final ObjectProperty<ObservableList<Klasse>> klassenProperty
            = new SimpleObjectProperty<>();

    public LehrerStundenplanViewModel(Lehrkraft lehrkraft) {
        this.model = new LehrerStundenplanModel(lehrkraft);
        this.model.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        gridProperty.set(model.getStundenGrid());

        // NEU: nur setzen wenn sich Klassen wirklich geändert haben
        List<Klasse> neueKlassen = model.getEigeneKlassen();
        if (klassenProperty.get() == null ||
                !klassenProperty.get().equals(
                        FXCollections.observableList(neueKlassen))) {
            klassenProperty.set(FXCollections.observableList(neueKlassen));
        }

        stundenListe.setAll(
                model.getStundenGrid().values().stream()
                        .flatMap(m -> m.values().stream())
                        .toList()
        );
    }

    public void laden()                    { model.laden(); }
    public void filterNachKlasse(Klasse k) { model.filterNachKlasse(k); }
    public Stundenplan getAktuellerPlan()  { return model.getAktuellerPlan(); }

    public int getStundenZaehler(Klasse klasse, Fach fach) {
        if (klasse == null || fach == null) return 0;
        String key = klasse.getId() + "_" + fach.getId();
        return model.getStundenZaehler().getOrDefault(key, 0);
    }

    public String getVertretungslehrerName(Stunde stunde) {
        return VertretungUtil.getVertretungslehrerName(
                stunde, model.getVertretungenProStunde()
        );
    }

    @Override
    public int getLehrkraftStunden(Lehrkraft lehrkraft) {
        if (lehrkraft == null) return 0;
        return model.getLehrkraftStunden().getOrDefault(lehrkraft.getId(), 0);
    }
}