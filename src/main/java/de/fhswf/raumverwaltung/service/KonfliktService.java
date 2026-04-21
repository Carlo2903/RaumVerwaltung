package de.fhswf.raumverwaltung.service;

import de.fhswf.raumverwaltung.db.dao.StundeDao;
import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.db.exception.PlanungException;
import java.util.List;

public class KonfliktService {
    private final StundeDao stundeDao = new StundeDao();

    public void validiereStunde(Stunde neueStunde) throws PlanungException {
        // Null-Checks zuerst
        if (neueStunde.getZeitslot() == null) {
            throw new PlanungException("Unvollständige Daten", "Zeitslot muss gesetzt sein.");
        }
        if (neueStunde.getLehrkraft() == null) {
            throw new PlanungException("Unvollständige Daten", "Lehrkraft muss gesetzt sein.");
        }
        if (neueStunde.getRaum() == null) {
            throw new PlanungException("Unvollständige Daten", "Raum muss gesetzt sein.");
        }
        if (neueStunde.getKlasse() == null) {
            throw new PlanungException("Unvollständige Daten", "Klasse muss gesetzt sein.");
        }

        List<Stunde> konflikte = stundeDao.findeKollisionen(
                neueStunde.getZeitslot(), neueStunde.getRaum(),
                neueStunde.getLehrkraft(), neueStunde.getKlasse()
        );

        // Bei einer bestehenden Stunde (id != null) findet die DB-Abfrage
        // die Stunde selbst – das ist kein echter Konflikt
        konflikte = konflikte.stream()
                .filter(k -> !k.getId().equals(neueStunde.getId()))
                .toList();

        if (!konflikte.isEmpty()) {
            Stunde k = konflikte.get(0);
            String grund;

            // FIX: else-if statt if/– erste Übereinstimmung gewinnt
            if (k.getLehrkraft().equals(neueStunde.getLehrkraft())) {
                grund = "Lehrkraft '" + k.getLehrkraft().getName() + "' hat zu diesem Zeitslot bereits Unterricht.";
            } else if (k.getRaum().equals(neueStunde.getRaum())) {
                grund = "Raum '" + k.getRaum().getBezeichnung() + "' ist zu diesem Zeitslot bereits belegt.";
            } else {
                grund = "Klasse '" + k.getKlasse().getBezeichnung() + "' hat zu diesem Zeitslot bereits Unterricht.";
            }

            throw new PlanungException("Stundenplan-Konflikt", grund);
        }
    }
}