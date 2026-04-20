package de.fhswf.raumverwaltung.ui.tabpane.schueler;

import de.fhswf.raumverwaltung.db.dao.StundeDao;
import de.fhswf.raumverwaltung.db.dao.StundenplanDao;
import de.fhswf.raumverwaltung.db.dao.SchuljahrDao;
import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.service.BenutzerService;
import lombok.Getter;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Optional;

public class SchuelerPortalModel extends Observable {

    private static SchuelerPortalModel instance;

    private final StundeDao      stundeDao      = new StundeDao();
    private final StundenplanDao stundenplanDao = new StundenplanDao();
    private final SchuljahrDao   schuljahrDao   = new SchuljahrDao();

    // Aktuell angezeigtes Datum
    @Getter
    private LocalDate aktuellesDatum = LocalDate.now();

    // Klasse des eingeloggten Schülers
    @Getter
    private Klasse aktuelleKlasse = null;

    // Stunden des aktuellen Tages
    @Getter
    private List<Stunde> tagesStunden = new ArrayList<>();

    // Stunden der aktuellen Woche (Mo–Fr)
    @Getter
    private List<Stunde> wochenStunden = new ArrayList<>();

    private SchuelerPortalModel() {}

    public static SchuelerPortalModel getInstance() {
        if (instance == null) {
            instance = new SchuelerPortalModel();
        }
        return instance;
    }

    // Initiales Laden – Klasse aus eingeloggtem Benutzer
    public void laden() {
        Benutzer benutzer = BenutzerService.getInstance().getAktuellerBenutzer();

        if (benutzer instanceof SchuelerBenutzer schueler) {
            aktuelleKlasse = schueler.getKlasse();
        }

        ladeAktuellenTag();
        ladeAktuelleWoche();
    }

    // Einen Tag vor / zurück navigieren
    public void navigiereTage(int tage) {
        aktuellesDatum = aktuellesDatum.plusDays(tage);

        // Wochenende überspringen
        if (aktuellesDatum.getDayOfWeek() == DayOfWeek.SATURDAY) {
            aktuellesDatum = aktuellesDatum.plusDays(tage > 0 ? 2 : -1);
        }
        if (aktuellesDatum.getDayOfWeek() == DayOfWeek.SUNDAY) {
            aktuellesDatum = aktuellesDatum.plusDays(tage > 0 ? 1 : -2);
        }

        ladeAktuellenTag();
        ladeAktuelleWoche();
    }

    // Zurück auf heute
    public void navigiereHeute() {
        aktuellesDatum = LocalDate.now();
        ladeAktuellenTag();
        ladeAktuelleWoche();
    }

    // ---------------------------------------------------------------
    // Private Hilfsmethoden
    // ---------------------------------------------------------------

    private void ladeAktuellenTag() {
        if (aktuelleKlasse == null) {
            tagesStunden = new ArrayList<>();
            setChanged();
            notifyObservers();
            return;
        }

        // Wochentag des aktuellen Datums bestimmen
        Wochentag wochentag = mappeWochentag(aktuellesDatum.getDayOfWeek());
        if (wochentag == null) {
            tagesStunden = new ArrayList<>();
            setChanged();
            notifyObservers();
            return;
        }

        // Stunden der Klasse an diesem Wochentag laden
        tagesStunden = ladeStundenFuerKlasseUndTag(aktuelleKlasse, wochentag);

        setChanged();
        notifyObservers();
    }

    private void ladeAktuelleWoche() {
        if (aktuelleKlasse == null) {
            wochenStunden = new ArrayList<>();
            return;
        }

        wochenStunden = new ArrayList<>();

        // Montag der aktuellen Woche
        LocalDate montag = aktuellesDatum.with(DayOfWeek.MONDAY);

        for (int i = 0; i < 5; i++) {
            LocalDate tag = montag.plusDays(i);
            Wochentag wochentag = mappeWochentag(tag.getDayOfWeek());
            if (wochentag != null) {
                wochenStunden.addAll(
                        ladeStundenFuerKlasseUndTag(aktuelleKlasse, wochentag)
                );
            }
        }
    }

    private List<Stunde> ladeStundenFuerKlasseUndTag(Klasse klasse,
                                                     Wochentag wochentag) {
        // Aktiven Stundenplan laden
        Optional<Schuljahr> schuljahr = schuljahrDao.findeAktives();
        if (schuljahr.isEmpty()) return new ArrayList<>();

        List<Stundenplan> plaene = stundenplanDao.findeNachSchuljahr(schuljahr.get());
        if (plaene.isEmpty()) return new ArrayList<>();

        // Stunden filtern: nur diese Klasse + dieser Wochentag
        return plaene.get(0).getStunden().stream()
                .filter(s -> s.getKlasse() != null &&
                        s.getKlasse().equals(klasse) &&
                        s.getZeitslot().getWochentag() == wochentag)
                .sorted((a, b) -> Integer.compare(
                        a.getZeitslot().getStundenNummer(),
                        b.getZeitslot().getStundenNummer()
                ))
                .toList();
    }

    // Java DayOfWeek → eigenes Wochentag-Enum
    private Wochentag mappeWochentag(DayOfWeek day) {
        return switch (day) {
            case MONDAY    -> Wochentag.MONTAG;
            case TUESDAY   -> Wochentag.DIENSTAG;
            case WEDNESDAY -> Wochentag.MITTWOCH;
            case THURSDAY  -> Wochentag.DONNERSTAG;
            case FRIDAY    -> Wochentag.FREITAG;
            default        -> null; // Wochenende
        };
    }
}