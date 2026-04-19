package de.fhswf.raumverwaltung.service;

import de.fhswf.raumverwaltung.db.dao.*;
import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.db.exception.PlanungException;
import java.time.LocalDate;
import java.util.List;

public class VertretungsService {

    private final AbwesenheitDao abwesenheitDao = new AbwesenheitDao();
    private final VertretungDao vertretungDao = new VertretungDao();
    private final StundeDao stundeDao = new StundeDao();

    // Schritt 1: Abwesenheit erfassen
    public Abwesenheit erfasseAbwesenheit(Lehrkraft lehrkraft, LocalDate von,
                                          LocalDate bis, VertretungsGrund grund,
                                          String bemerkung) throws PlanungException {
        if (von.isAfter(bis)) {
            throw new PlanungException("Ungültige Eingabe",
                    "Das Von-Datum darf nicht nach dem Bis-Datum liegen.");
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

    // Schritt 2: Betroffene Stunden ermitteln
    public List<Stunde> findeBetroffeneStunden(Abwesenheit abwesenheit) {
        return stundeDao.findeNachLehrkraftUndZeitraum(
                abwesenheit.getLehrkraft(),
                abwesenheit.getVon(),
                abwesenheit.getBis()
        );
    }

    // Schritt 3: Verfügbare Vertretungslehrer für einen Slot
    public List<Lehrkraft> findeVertretungskandidaten(Zeitslot zeitslot, LocalDate datum) {
        return vertretungDao.findeVerfuegbareLehrer(zeitslot, datum);
    }

    // Schritt 4: Vertretung zuweisen
    public Vertretung weiseVertretungZu(Stunde stunde, Lehrkraft vertretungsLehrer,
                                        LocalDate datum, VertretungsGrund grund,
                                        String bemerkung) throws PlanungException {
        // Prüfen ob der Lehrer wirklich frei ist
        List<Lehrkraft> kandidaten = findeVertretungskandidaten(stunde.getZeitslot(), datum);
        if (!kandidaten.contains(vertretungsLehrer)) {
            throw new PlanungException("Konflikt",
                    "Die gewählte Lehrkraft ist zu diesem Zeitslot nicht verfügbar.");
        }

        Vertretung vertretung = Vertretung.builder()
                .stunde(stunde)
                .vertretungsLehrer(vertretungsLehrer)
                .datum(datum)
                .grund(grund)
                .bemerkung(bemerkung)
                .build();

        vertretungDao.persist(vertretung);
        return vertretung;
    }
}