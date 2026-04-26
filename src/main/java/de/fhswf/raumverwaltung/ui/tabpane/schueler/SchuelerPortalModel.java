package de.fhswf.raumverwaltung.ui.tabpane.schueler;

import de.fhswf.raumverwaltung.db.dao.*;
import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.service.BenutzerService;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class SchuelerPortalModel extends Observable {

    private static SchuelerPortalModel instance;

    private final StundeDao      stundeDao      = new StundeDao();
    private final StundenplanDao stundenplanDao = new StundenplanDao();
    private final SchuljahrDao   schuljahrDao   = new SchuljahrDao();
    private final VertretungDao  vertretungDao  = new VertretungDao();

    @Getter
    private LocalDate aktuellesDatum = LocalDate.now();

    @Getter
    private Klasse aktuelleKlasse = null;

    @Getter
    private List<Stunde> tagesStunden = new ArrayList<>();

    @Getter
    private List<Stunde> wochenStunden = new ArrayList<>();

    // NEU: Vertretungen für Anzeige
    @Getter
    private Map<Long, Vertretung> vertretungenProStunde = new HashMap<>();

    // Gecachter Plan – nur einmal laden
    private Stundenplan aktuellerPlan = null;

    private List<Stunde> alleStunden = new ArrayList<>();


    private SchuelerPortalModel() {}

    public static SchuelerPortalModel getInstance() {
        if (instance == null) {
            instance = new SchuelerPortalModel();
        }
        return instance;
    }

    public void laden() {
        Benutzer benutzer = BenutzerService.getInstance().getAktuellerBenutzer();
        if (benutzer instanceof SchuelerBenutzer schueler) {
            aktuelleKlasse = schueler.getKlasse();
        }

        schuljahrDao.clearCache();
        stundenplanDao.clearCache();
        Optional<Schuljahr> schuljahr = schuljahrDao.findeAktives();
        if (schuljahr.isEmpty()) return;

        List<Stundenplan> plaene = stundenplanDao.findeNachSchuljahr(schuljahr.get());
        if (plaene.isEmpty()) return;

        aktuellerPlan = plaene.get(0);

        // NEU: alle Stunden einmal laden
        stundeDao.clearCache();
        alleStunden = stundeDao.findeNachStundenplan(aktuellerPlan);

        ladeVertretungen();
        ladeAktuellenTag();
        ladeAktuelleWoche();
    }

    public void navigiereTage(int tage) {
        aktuellesDatum = aktuellesDatum.plusDays(tage);

        if (aktuellesDatum.getDayOfWeek() == DayOfWeek.SATURDAY) {
            aktuellesDatum = aktuellesDatum.plusDays(tage > 0 ? 2 : -1);
        }
        if (aktuellesDatum.getDayOfWeek() == DayOfWeek.SUNDAY) {
            aktuellesDatum = aktuellesDatum.plusDays(tage > 0 ? 1 : -2);
        }

        ladeAktuellenTag();
        ladeAktuelleWoche();
    }

    public void navigiereHeute() {
        aktuellesDatum = LocalDate.now();
        ladeAktuellenTag();
        ladeAktuelleWoche();
    }

    // ---------------------------------------------------------------
    // Private Hilfsmethoden
    // ---------------------------------------------------------------

    private void ladeVertretungen() {
        vertretungenProStunde = new HashMap<>();
        if (aktuellerPlan == null) return;

        vertretungDao.findeNachStundenplan(aktuellerPlan)
                .forEach(v -> vertretungenProStunde.put(v.getStunde().getId(), v));
    }

    private void ladeAktuellenTag() {
        if (aktuelleKlasse == null || aktuellerPlan == null) {
            tagesStunden = new ArrayList<>();
            setChanged();
            notifyObservers();
            return;
        }

        Wochentag wochentag = mappeWochentag(aktuellesDatum.getDayOfWeek());
        if (wochentag == null) {
            tagesStunden = new ArrayList<>();
            setChanged();
            notifyObservers();
            return;
        }

        // NEU: aktuellesDatum als drittes Argument
        tagesStunden = filterStunden(aktuelleKlasse, wochentag, aktuellesDatum);
        setChanged();
        notifyObservers();
    }

    private void ladeAktuelleWoche() {
        if (aktuelleKlasse == null || aktuellerPlan == null) {
            wochenStunden = new ArrayList<>();
            return;
        }

        wochenStunden = new ArrayList<>();
        LocalDate montag = aktuellesDatum.with(DayOfWeek.MONDAY);

        for (int i = 0; i < 5; i++) {
            LocalDate tag = montag.plusDays(i);
            Wochentag wochentag = mappeWochentag(tag.getDayOfWeek());
            if (wochentag != null) {
                wochenStunden.addAll(filterStunden(aktuelleKlasse, wochentag, tag));
            }
        }
    }
    private List<Stunde> filterStunden(Klasse klasse, Wochentag wochentag, LocalDate datum) {
        return alleStunden.stream()
                .filter(s -> s.getKlasse() != null &&
                        s.getKlasse().getId().equals(klasse.getId()) &&
                        s.getZeitslot().getWochentag() == wochentag)
                .sorted(Comparator.comparingInt(s -> s.getZeitslot().getStundenNummer()))
                .toList();
    }

    // Vertretung nur wenn Datum in Abwesenheitszeitraum liegt
    public boolean hatVertretungAmDatum(Stunde stunde, LocalDate datum) {
        Vertretung v = vertretungenProStunde.get(stunde.getId());
        if (v == null) return false;
        return v.getDatum().equals(datum);
    }


    private List<Stunde> ladeStundenFuerKlasseUndTag(Klasse klasse,
                                                     Wochentag wochentag) {
        stundeDao.clearCache();

        // NEU: stundeDao.findeNachStundenplan() statt getStunden()
        return stundeDao.findeNachStundenplan(aktuellerPlan).stream()
                .filter(s -> s.getKlasse() != null &&
                        s.getKlasse().getId().equals(klasse.getId()) &&
                        s.getZeitslot().getWochentag() == wochentag)
                .sorted(Comparator.comparingInt(
                        s -> s.getZeitslot().getStundenNummer()
                ))
                .toList();
    }

    private Wochentag mappeWochentag(DayOfWeek day) {
        return switch (day) {
            case MONDAY    -> Wochentag.MONTAG;
            case TUESDAY   -> Wochentag.DIENSTAG;
            case WEDNESDAY -> Wochentag.MITTWOCH;
            case THURSDAY  -> Wochentag.DONNERSTAG;
            case FRIDAY    -> Wochentag.FREITAG;
            default        -> null;
        };
    }

     public void navigiereWochen(int wochen) {
        aktuellesDatum = aktuellesDatum.plusWeeks(wochen);
        ladeAktuellenTag();
        ladeAktuelleWoche();
    }
}