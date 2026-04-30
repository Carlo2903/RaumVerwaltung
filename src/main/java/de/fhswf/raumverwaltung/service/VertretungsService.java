package de.fhswf.raumverwaltung.service;

import de.fhswf.raumverwaltung.db.dao.*;
import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.db.exception.PlanungException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VertretungsService {

    // Singleton – konsistent zu BenutzerService
    private static VertretungsService instance;

    private final AbwesenheitDao abwesenheitDao = new AbwesenheitDao();
    private final VertretungDao  vertretungDao  = new VertretungDao();
    private final StundeDao      stundeDao      = new StundeDao();


    private VertretungsService() {}

    public static VertretungsService getInstance() {
        if (instance == null) {
            instance = new VertretungsService();
        }
        return instance;
    }

    // Schritt 1: Abwesenheit erfassen
    public Abwesenheit erfasseAbwesenheit(Lehrkraft lehrkraft, LocalDate von,
                                          LocalDate bis, VertretungsGrund grund,
                                          String bemerkung) throws PlanungException {
        if (von.isAfter(bis)) {
            throw new PlanungException(
                    "Ungültige Eingabe",
                    "Das Von-Datum darf nicht nach dem Bis-Datum liegen."
            );
        }

        Abwesenheit abwesenheit = Abwesenheit.builder()
                .lehrkraft(lehrkraft)
                .von(von)
                .bis(bis)
                .grund(grund)
                .bemerkung(bemerkung)
                .aktiv(true)
                .build();

        abwesenheitDao.persist(abwesenheit);
        return abwesenheit;
    }

    // Schritt 2: Betroffene Stunden ermitteln (über Wochentage, nicht Datum)
    public List<Stunde> findeBetroffeneStunden(Abwesenheit abwesenheit) {
        List<Wochentag> betroffeneTage = berechneWochentage(
                abwesenheit.getVon(),
                abwesenheit.getBis()
        );
        return stundeDao.findeNachLehrkraftUndWochentage(
                abwesenheit.getLehrkraft(),
                betroffeneTage
        );
    }

    // Schritt 3: Verfügbare Vertretungslehrer für einen Zeitslot und Datum
    public List<Lehrkraft> findeVertretungskandidaten(Zeitslot zeitslot, LocalDate datum) {
        List<Lehrkraft> verfuegbare = vertretungDao.findeVerfuegbareLehrer(zeitslot, datum);

        // NEU: Lehrkräfte filtern die Sollstunden noch nicht erreicht haben
        return verfuegbare.stream()
                .filter(l -> !hatSollstundenErreicht(l))
                .toList();
    }

    private boolean hatSollstundenErreicht(Lehrkraft lehrkraft) {
        if (lehrkraft.getSollStunden() <= 0) return false;

        long aktuelleStunden = stundeDao.findAll().stream()
                .filter(s -> s.getLehrkraft() != null &&
                        s.getLehrkraft().getId().equals(lehrkraft.getId()))
                .count();

        return aktuelleStunden >= lehrkraft.getSollStunden();
    }
    // Schritt 4: Vertretung zuweisen
    public Vertretung weiseVertretungZu(Stunde stunde, Lehrkraft vertretungsLehrer,
                                        LocalDate datum, VertretungsGrund grund,
                                        String bemerkung) throws PlanungException {
        List<Lehrkraft> kandidaten = findeVertretungskandidaten(stunde.getZeitslot(), datum);

        // ID-Vergleich statt Objekt-Vergleich – verschiedene Hibernate-Instanzen
        boolean istVerfuegbar = kandidaten.stream()
                .anyMatch(k -> k.getId().equals(vertretungsLehrer.getId()));

        if (!istVerfuegbar) {
            throw new PlanungException(
                    "Konflikt",
                    "Die Lehrkraft '" + vertretungsLehrer.getName() +
                            "' ist zu diesem Zeitslot nicht verfügbar."
            );
        }

        Vertretung vertretung = Vertretung.builder()
                .stunde(stunde)
                .vertretungsLehrer(vertretungsLehrer)
                .datum(datum)
                .grund(grund)
                .bemerkung(bemerkung)
                .build();

        vertretungDao.persist(vertretung);

        stunde.setIstVertretung(true);
        stundeDao.merge(stunde);

        return vertretung;
    }

    // Hilfsmethode: Wochentage zwischen zwei Daten berechnen
    private List<Wochentag> berechneWochentage(LocalDate von, LocalDate bis) {
        List<Wochentag> tage = new ArrayList<>();
        LocalDate current = von;

        while (!current.isAfter(bis)) {
            switch (current.getDayOfWeek()) {
                case MONDAY    -> tage.add(Wochentag.MONTAG);
                case TUESDAY   -> tage.add(Wochentag.DIENSTAG);
                case WEDNESDAY -> tage.add(Wochentag.MITTWOCH);
                case THURSDAY  -> tage.add(Wochentag.DONNERSTAG);
                case FRIDAY    -> tage.add(Wochentag.FREITAG);
                default        -> {} // Samstag/Sonntag ignorieren
            }
            current = current.plusDays(1);
        }

        // distinct() – falls Abwesenheit mehrere Wochen geht
        return tage.stream().distinct().collect(Collectors.toList());
    }

    public LocalDate berechneStundenDatum(Stunde stunde, LocalDate abwesenheitVon) {
        Wochentag wochentag = stunde.getZeitslot().getWochentag();
        LocalDate montag = abwesenheitVon.with(DayOfWeek.MONDAY);

        return switch (wochentag) {
            case MONTAG     -> montag;
            case DIENSTAG   -> montag.plusDays(1);
            case MITTWOCH   -> montag.plusDays(2);
            case DONNERSTAG -> montag.plusDays(3);
            case FREITAG    -> montag.plusDays(4);
        };
    }
}