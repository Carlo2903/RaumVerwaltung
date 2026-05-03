package de.fhswf.raumverwaltung.ui.tabpane.stundenplan;

import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.service.KonfliktService;
import de.fhswf.raumverwaltung.db.exception.PlanungException;
import de.fhswf.raumverwaltung.ui.util.VertretungUtil;
import javafx.beans.property.*;
import javafx.collections.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.*;

public class StundenplanViewModel implements Observer, StundenplanViewModelInterface {

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
        gridProperty.set(model.getStundenGrid());

        // NEU: nur setzen wenn sich Klassen wirklich geändert haben
        List<Klasse> neueKlassen = model.getAlleKlassen();
        if (klassenProperty.get() == null ||
                !klassenProperty.get().containsAll(neueKlassen) ||
                !neueKlassen.containsAll(klassenProperty.get())) {
            klassenProperty.set(
                    FXCollections.observableList(neueKlassen)
            );
        }

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
    public Klasse getAktuelleKlasse()          { return model.getAktuelleKlasse(); }

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
        return model.findeZeitslot(tag, stundenNummer);
    }

    public int getStundenZaehler(Klasse klasse, Fach fach) {
        if (klasse == null || fach == null) return 0;
        String key = klasse.getId() + "_" + fach.getId();
        return model.getStundenZaehler().getOrDefault(key, 0);
    }

    // Mit Datum – für SchuelerPortalView:
    public String getVertretungslehrerName(Stunde stunde, LocalDate datum) {
        return VertretungUtil.getVertretungslehrerName(
                stunde, model.getVertretungenProStunde(), datum
        );
    }

    // Ohne Datum – für StundenplanRasterView:
    public String getVertretungslehrerName(Stunde stunde) {
        return VertretungUtil.getVertretungslehrerName(
                stunde, model.getVertretungenProStunde()
        );
    }

    public int getLehrkraftStunden(Lehrkraft lehrkraft) {
        if (lehrkraft == null) return 0;
        return model.getLehrkraftStunden().getOrDefault(lehrkraft.getId(), 0);
    }

    /**
     * Öffnet den Bearbeiten-Dialog für eine Stunde (Admin-Implementierung).
     * Wird von der View über das Interface aufgerufen – kein instanceof-Cast nötig.
     */
    @Override
    public void zeigeBearbeitenDialog(Stunde stunde, Wochentag tag, int stundenNummer) {
        StundeBearbeitenDialog.zeige(this, stunde, tag, stundenNummer);
    }
}