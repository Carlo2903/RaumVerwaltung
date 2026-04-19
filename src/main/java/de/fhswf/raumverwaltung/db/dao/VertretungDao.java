package de.fhswf.raumverwaltung.db.dao;

import de.fhswf.raumverwaltung.db.entities.Vertretung;
import de.fhswf.raumverwaltung.db.entities.Zeitslot;
import de.fhswf.raumverwaltung.db.entities.Lehrkraft;
import java.time.LocalDate;
import java.util.List;

public class VertretungDao extends GenericDao<Vertretung> {

    // Alle Vertretungen eines bestimmten Datums
    public List<Vertretung> findeNachDatum(LocalDate datum) {
        return entityManager
                .createQuery(
                        "SELECT v FROM Vertretung v WHERE v.datum = :datum",
                        Vertretung.class)
                .setParameter("datum", datum)
                .getResultList();
    }

    // Verfügbare Vertretungslehrer für einen Zeitslot
    public List<Lehrkraft> findeVerfuegbareLehrer(Zeitslot zeitslot, LocalDate datum) {
        return entityManager
                .createQuery(
                        "SELECT l FROM Lehrkraft l WHERE l NOT IN (" +
                                "  SELECT s.lehrkraft FROM Stunde s WHERE s.zeitslot = :slot" +
                                ") AND l NOT IN (" +
                                "  SELECT a.lehrkraft FROM Abwesenheit a WHERE :datum BETWEEN a.von AND a.bis" +
                                ")",
                        Lehrkraft.class)
                .setParameter("slot", zeitslot)
                .setParameter("datum", datum)
                .getResultList();
    }
}