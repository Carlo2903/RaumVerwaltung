package de.fhswf.raumverwaltung.service;

import de.fhswf.raumverwaltung.db.dao.SperrzeitDao;
import de.fhswf.raumverwaltung.db.dao.StundeDao;
import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.db.exception.PlanungException;
import java.util.List;

public class KonfliktService {
    private final StundeDao stundeDao;
    private final SperrzeitDao sperrzeitDao;

    /** Produktions-Konstruktor: erzeugt DAOs selbst. */
    public KonfliktService() {
        this.stundeDao    = new StundeDao();
        this.sperrzeitDao = new SperrzeitDao();
    }

    /**
     * Test-Konstruktor: DAOs werden von außen übergeben (Dependency Injection).
     * Package-private – nur für Unit-Tests im selben Paket sichtbar.
     */
    KonfliktService(StundeDao stundeDao, SperrzeitDao sperrzeitDao) {
        this.stundeDao    = stundeDao;
        this.sperrzeitDao = sperrzeitDao;
    }


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

        validiereLeerkraftStunden(neueStunde);
        validiereSperrzeiten(neueStunde);
    }


    private void validiereSperrzeiten(Stunde neueStunde) throws PlanungException {
        if (neueStunde.getLehrkraft() == null ||
                neueStunde.getZeitslot() == null) return;

        boolean istGesperrt = sperrzeitDao
                .findeNachLehrkraft(neueStunde.getLehrkraft())
                .stream()
                .anyMatch(sz -> sz.getZeitslot().getId()
                        .equals(neueStunde.getZeitslot().getId()));

        if (istGesperrt) {
            throw new PlanungException(
                    "Sperrzeit",
                    "Lehrkraft '" + neueStunde.getLehrkraft().getName() +
                            "' hat zu diesem Zeitslot eine Sperrzeit eingetragen."
            );
        }
    }

    private void validiereLeerkraftStunden(Stunde neueStunde) throws PlanungException {
        if (neueStunde.getLehrkraft() == null) return;

        int sollStunden = neueStunde.getLehrkraft().getSollStunden();
        if (sollStunden <= 0) return;

        // Wie viele Stunden hat die Lehrkraft bereits?
        long aktuelleStunden = stundeDao
                .findeNachStundenplan(neueStunde.getStundenplan()).stream()
                .filter(s -> s.getLehrkraft() != null &&
                        s.getLehrkraft().getId()
                                .equals(neueStunde.getLehrkraft().getId()))
                // Bei Bearbeitung: eigene Stunde nicht mitzählen
                .filter(s -> neueStunde.getId() == null ||
                        !s.getId().equals(neueStunde.getId()))
                .count();

        if (aktuelleStunden >= sollStunden) {
            throw new PlanungException(
                    "Wochenstunden überschritten",
                    "Lehrkraft '" + neueStunde.getLehrkraft().getName() +
                            "' hat bereits " + aktuelleStunden + " von " +
                            sollStunden + " Wochenstunden."
            );
        }
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