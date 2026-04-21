package de.fhswf.raumverwaltung.ui.tabpane.stundenplan;

import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.service.KonfliktService;
import de.fhswf.raumverwaltung.db.exception.PlanungException;
import javafx.beans.property.*;
import javafx.collections.*;
import lombok.Getter;

import java.util.*;

public class StundenplanViewModel implements Observer {

    private final StundenplanTableModel model;

    @Getter
    private final ObjectProperty<Map<Wochentag, Map<Integer, Stunde>>> gridProperty
            = new SimpleObjectProperty<>();

    @Getter
    private final ObjectProperty<ObservableList<Klasse>> klassenProperty
            = new SimpleObjectProperty<>();
    @Getter
    private final ObservableList<Stunde> stundenListe
            = FXCollections.observableArrayList();

    private final KonfliktService konfliktService = new KonfliktService();

    public StundenplanViewModel() {
        this.model = StundenplanTableModel.getInstance();
        this.model.addObserver(this);
    }


    @Override
    public void update(Observable o, Object arg) {
        // gridProperty bleibt für das Grid
        gridProperty.set(model.getStundenGrid());
        klassenProperty.set(
                FXCollections.observableList(model.getAlleKlassen())
        );
        // setAll() feuert immer
        stundenListe.setAll(
                model.getStundenGrid().values().stream()
                        .flatMap(m -> m.values().stream())
                        .toList()
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

    public List<Lehrkraft> findeLehrkraefteNachFach(Fach fach) {
        if (fach == null) return List.of();
        return model.getAlleLehrkraefte().stream()
                .filter(lk -> lk.getFaecher().contains(fach))
                .toList();
    }

    public List<Fach> getAlleFaecher() {
        return model.getAlleFaecher();
    }

    public List<Raum> getAlleRaeume() {
        return model.getAlleRaeume();
    }

    public Optional<Zeitslot> findeZeitslot(Wochentag tag, int stundenNummer) {
        return model.getAlleZeitslots().stream()
                .filter(z -> z.getWochentag() == tag
                        && z.getStundenNummer() == stundenNummer)
                .findFirst();
    }

}