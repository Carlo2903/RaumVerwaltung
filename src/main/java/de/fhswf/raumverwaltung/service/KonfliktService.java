package de.fhswf.raumverwaltung.service;

import de.fhswf.raumverwaltung.db.dao.StundeDao;
import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.db.exception.PlanungException;
import java.util.List;

public class KonfliktService {
    private final StundeDao stundeDao = new StundeDao();

    public void validiereStunde(Stunde neueStunde) throws PlanungException {
        // 1. Basis-Check: Sind alle Daten da?
        if (neueStunde.getZeitslot() == null || neueStunde.getLehrkraft() == null) {
            throw new PlanungException("Unvollständige Daten", "Zeitslot und Lehrkraft müssen gesetzt sein.");
        }

        // 2. Datenbank-Check: Gibt es Überschneidungen?
        List<Stunde> konflikte = stundeDao.findeKollisionen(
                neueStunde.getZeitslot(), neueStunde.getRaum(),
                neueStunde.getLehrkraft(), neueStunde.getKlasse()
        );

        if (!konflikte.isEmpty()) {
            Stunde k = konflikte.get(0);
            String grund = "";
            if (k.getRaum().equals(neueStunde.getRaum())) grund = "Raum belegt";
            if (k.getLehrkraft().equals(neueStunde.getLehrkraft())) grund = "Lehrer bereits im Unterricht";
            if (k.getKlasse().equals(neueStunde.getKlasse())) grund = "Klasse hat bereits Unterricht";

            throw new PlanungException("Stundenplan-Konflikt", grund);
        }
    }
}