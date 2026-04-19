package de.fhswf.raumverwaltung.db.dao;

import de.fhswf.raumverwaltung.db.entities.Wochentag;
import de.fhswf.raumverwaltung.db.entities.Zeitslot;
import java.util.List;

public class ZeitslotDao extends GenericDao<Zeitslot> {

    // Alle Zeitslots eines Tages, sortiert nach Stundennummer
    public List<Zeitslot> findeNachWochentag(Wochentag wochentag) {
        return entityManager
                .createQuery(
                        "SELECT z FROM Zeitslot z WHERE z.wochentag = :tag ORDER BY z.stundenNummer",
                        Zeitslot.class)
                .setParameter("tag", wochentag)
                .getResultList();
    }
}