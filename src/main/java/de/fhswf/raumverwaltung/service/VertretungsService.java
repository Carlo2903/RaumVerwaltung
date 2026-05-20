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

    private static VertretungsService instance;

    private final AbwesenheitDao abwesenheitDao = new AbwesenheitDao();
    private final VertretungDao  vertretungDao  = new VertretungDao();
    private final StundeDao      stundeDao      = new StundeDao();
    private final SperrzeitDao sperrzeitDao = new SperrzeitDao();


    private VertretungsService() {}

    public static VertretungsService getInstance() {
        if (instance == null) {
            instance = new VertretungsService();
        }
        return instance;
    }

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

    public List<Lehrkraft> findeVertretungskandidaten(Zeitslot zeitslot,
                                                      LocalDate datum) {
        List<Lehrkraft> verfuegbare =
                vertretungDao.findeVerfuegbareLehrer(zeitslot, datum);

        List<Long> gesperrteLehrkraftIds =
                sperrzeitDao.findeNachZeitslot(zeitslot).stream()
                        .map(sz -> sz.getLehrkraft().getId())
                        .toList();


        return verfuegbare.stream()
                .filter(l -> !hatSollstundenErreicht(l))
                .filter(l -> !hatSperrzeit(l, zeitslot))
                .toList();
    }

    private boolean hatSperrzeit(Lehrkraft lehrkraft, Zeitslot zeitslot) {
        return sperrzeitDao.findeNachLehrkraft(lehrkraft).stream()
                .anyMatch(sz -> sz.getZeitslot().getId()
                        .equals(zeitslot.getId()));
    }

    private boolean hatSollstundenErreicht(Lehrkraft lehrkraft) {
        if (lehrkraft.getSollStunden() <= 0) return false;

        long aktuelleStunden = stundeDao.zaehleBelegtStunden(lehrkraft);
        return aktuelleStunden >= lehrkraft.getSollStunden();
    }
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

    public Vertretung weiseAusfallZu(Stunde stunde, LocalDate datum, 
                                     VertretungsGrund grund, String bemerkung) throws PlanungException {
        // Ein Ausfall ist technisch eine Vertretung OHNE Vertretungslehrer (null)
        Vertretung vertretung = Vertretung.builder()
                .stunde(stunde)
                .vertretungsLehrer(null)
                .datum(datum)
                .grund(grund)
                .bemerkung(bemerkung)
                .build();

        vertretungDao.persist(vertretung);

        stunde.setIstAusfall(true); // Aktualisiert das Flag im globalen Raster für die Lehrersicht
        stundeDao.merge(stunde);

        return vertretung;
    }

    public void loescheVertretung(Vertretung vertretung) {
        vertretungDao.loescheVertretungMitStundenReset(vertretung);
    }

    /**
     * Löscht eine Abwesenheit vollständig inkl. aller zugehörigen Vertretungen.
     * Ablauf: erst alle Vertretungen (mit Stunden-Reset), dann die Abwesenheit selbst.
     */
    public void loescheAbwesenheitKomplett(Abwesenheit abwesenheit) {
        List<Stunde> betroffene = findeBetroffeneStunden(abwesenheit);

        if (!betroffene.isEmpty()) {
            vertretungDao.findeNachStunden(betroffene)
                    .forEach(vertretungDao::loescheVertretungMitStundenReset);
        }

        abwesenheitDao.remove(abwesenheit);
    }

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