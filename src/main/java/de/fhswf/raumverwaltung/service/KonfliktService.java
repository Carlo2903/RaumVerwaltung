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
        validiereWochenstunden(neueStunde);
    }


    private void validiereWochenstunden(Stunde neueStunde) throws PlanungException {
        if (neueStunde.getFach() == null || neueStunde.getKlasse() == null) return;

        int maxStunden = neueStunde.getFach().getWochenstundenProKlasse();

        long bereitsVorhanden = stundeDao
                .findeNachKlasseUndFach(
                        neueStunde.getKlasse(),
                        neueStunde.getFach()
                )
                .stream()
                // NEU: null-sicherer Vergleich
                .filter(s -> neueStunde.getId() == null
                        || !s.getId().equals(neueStunde.getId()))
                .count();

        if (bereitsVorhanden >= maxStunden) {
            throw new PlanungException(
                    "Wochenstunden überschritten",
                    "Klasse '" + neueStunde.getKlasse().getBezeichnung() +
                            "' hat bereits " + bereitsVorhanden + " von " +
                            maxStunden + " Wochenstunden in '" +
                            neueStunde.getFach().getBezeichnung() + "'."
            );
        }
    }
}