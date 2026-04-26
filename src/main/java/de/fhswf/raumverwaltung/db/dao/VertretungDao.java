package de.fhswf.raumverwaltung.db.dao;

import de.fhswf.raumverwaltung.db.entities.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VertretungDao extends GenericDao<Vertretung> {

    public List<Vertretung> findeNachDatum(LocalDate datum) {
        return entityManager
                .createQuery(
                        "SELECT v FROM Vertretung v WHERE v.datum = :datum",
                        Vertretung.class)
                .setParameter("datum", datum)
                .getResultList();
    }

    // Lehrkräfte die im Zeitslot NICHT belegt und NICHT abwesend sind
    public List<Lehrkraft> findeVerfuegbareLehrer(Zeitslot zeitslot, LocalDate datum) {
        return entityManager
                .createQuery(
                        "SELECT l FROM Lehrkraft l " +
                                "WHERE l NOT IN (" +
                                "  SELECT s.lehrkraft FROM Stunde s WHERE s.zeitslot = :slot" +
                                ") " +
                                "AND l NOT IN (" +
                                "  SELECT a.lehrkraft FROM Abwesenheit a " +
                                "  WHERE :datum BETWEEN a.von AND a.bis AND a.aktiv = true" +
                                ")",
                        Lehrkraft.class)
                .setParameter("slot", zeitslot)
                .setParameter("datum", datum)
                .getResultList();
    }

    public List<Vertretung> findeNachStunden(List<Stunde> stunden) {
        if (stunden.isEmpty()) return new ArrayList<>();
        clearCache();
        return entityManager
                .createQuery(
                        "SELECT v FROM Vertretung v WHERE v.stunde IN :stunden",
                        Vertretung.class)
                .setParameter("stunden", stunden)
                .getResultList();
    }

    public List<Vertretung> findeNachStundenplan(Stundenplan stundenplan) {
        clearCache();
        return entityManager
                .createQuery(
                        "SELECT v FROM Vertretung v " +
                                "LEFT JOIN FETCH v.vertretungsLehrer " +
                                "LEFT JOIN FETCH v.stunde " +
                                "WHERE v.stunde.stundenplan = :plan",
                        Vertretung.class)
                .setParameter("plan", stundenplan)
                .getResultList();
    }
}