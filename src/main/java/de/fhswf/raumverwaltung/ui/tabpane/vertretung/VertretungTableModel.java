package de.fhswf.raumverwaltung.ui.tabpane.vertretung;

import de.fhswf.raumverwaltung.db.dao.AbwesenheitDao;
import de.fhswf.raumverwaltung.db.dao.LehrkraftDao;
import de.fhswf.raumverwaltung.db.dao.VertretungDao;
import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.db.exception.PlanungException;
import de.fhswf.raumverwaltung.service.VertretungsService;
import lombok.Getter;

import java.time.LocalDate;
import java.util.*;

public class VertretungTableModel extends Observable {

    private static VertretungTableModel instance;

    private final VertretungsService vertretungsService = VertretungsService.getInstance();
    private final LehrkraftDao       lehrkraftDao       = new LehrkraftDao();
    private final AbwesenheitDao     abwesenheitDao     = new AbwesenheitDao();

    // Alle Lehrkräfte für die Dropdown
    @Getter
    private List<Lehrkraft> alleLehrkraefte = new ArrayList<>();

    // Aktuell erfasste Abwesenheit
    @Getter
    private Abwesenheit aktuelleAbwesenheit = null;

    // Betroffene Stunden der aktuellen Abwesenheit
    @Getter
    private List<Stunde> betroffeneStunden = new ArrayList<>();

    // Aktuell ausgewählte Stunde (für Kandidaten-Anzeige)
    @Getter
    private Stunde ausgewaehlteStunde = null;

    // Verfügbare Vertretungslehrer für ausgewählte Stunde
    @Getter
    private List<Lehrkraft> verfuegbareLehrer = new ArrayList<>();

    @Getter
    private Map<Long, Vertretung> vertretungenProStunde = new HashMap<>();

    private final VertretungDao vertretungDao = new VertretungDao();

    private VertretungTableModel() {}

    public static VertretungTableModel getInstance() {
        if (instance == null) {
            instance = new VertretungTableModel();
        }
        return instance;
    }

    // Initiales Laden
    public void laden() {
        alleLehrkraefte = lehrkraftDao.findAll();
        setChanged();
        notifyObservers();
    }

    // Schritt 1: Abwesenheit erfassen
    public void abwesenheitErfassen(Lehrkraft lehrkraft, LocalDate von,
                                    LocalDate bis, VertretungsGrund grund,
                                    String bemerkung) throws PlanungException {
        aktuelleAbwesenheit = vertretungsService.erfasseAbwesenheit(
                lehrkraft, von, bis, grund, bemerkung
        );
        betroffeneStunden = vertretungsService.findeBetroffeneStunden(
                aktuelleAbwesenheit
        );
        ausgewaehlteStunde  = null;
        verfuegbareLehrer   = new ArrayList<>();
        ladeVertretungen();
        setChanged();
        notifyObservers();
    }

    // Schritt 2: Stunde auswählen → Kandidaten laden
    public void stundeAuswaehlen(Stunde stunde, LocalDate datum) {
        ausgewaehlteStunde = stunde;
        verfuegbareLehrer  = vertretungsService.findeVertretungskandidaten(
                stunde.getZeitslot(), datum
        );
        setChanged();
        notifyObservers();
    }

    // Schritt 3: Vertretung zuweisen
    public void vertretungZuweisen(Lehrkraft vertretungsLehrer,
                                   LocalDate datum) throws PlanungException {
        if (ausgewaehlteStunde == null) return;

        // Service kümmert sich um alles – inklusive istVertretung setzen
        vertretungsService.weiseVertretungZu(
                ausgewaehlteStunde, vertretungsLehrer,
                datum, aktuelleAbwesenheit.getGrund(),
                aktuelleAbwesenheit.getBemerkung()
        );

        betroffeneStunden = vertretungsService.findeBetroffeneStunden(
                aktuelleAbwesenheit
        );
        ladeVertretungen();
        ausgewaehlteStunde = null;
        verfuegbareLehrer  = new ArrayList<>();
        setChanged();
        notifyObservers();
    }
    private void ladeVertretungen() {
        vertretungenProStunde = new HashMap<>();
        if (betroffeneStunden.isEmpty()) return;

        // Alle Vertretungen für alle betroffenen Stunden laden
        vertretungDao.findeNachStunden(betroffeneStunden)
                .forEach(v -> vertretungenProStunde.put(v.getStunde().getId(), v));
    }



}