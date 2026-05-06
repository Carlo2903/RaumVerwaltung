package de.fhswf.raumverwaltung.db.dao;

import de.fhswf.raumverwaltung.db.entities.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        clearCache();
        return entityManager
                .createQuery(
                        "SELECT DISTINCT l FROM Lehrkraft l " +
                                "LEFT JOIN FETCH l.faecher " +
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

    public List<Vertretung> findeAlleAktiven() {
        clearCache();
        return entityManager
                .createQuery(
                        "SELECT v FROM Vertretung v " +
                                "LEFT JOIN FETCH v.stunde " +
                                "LEFT JOIN FETCH v.vertretungsLehrer",
                        Vertretung.class)
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

    public Optional<Vertretung> findeNachStunde(Stunde stunde) {
        clearCache();
        return entityManager
                .createQuery(
                        "SELECT v FROM Vertretung v " +
                                "LEFT JOIN FETCH v.vertretungsLehrer " +
                                "WHERE v.stunde = :stunde",
                        Vertretung.class)
                .setParameter("stunde", stunde)
                .getResultStream()
                .findFirst();
    }

    public void loescheVertretungMitStundenReset(Vertretung vertretung) {
        try {
            entityManager.getTransaction().begin();

            // Stunde zurücksetzen (sowohl Vertretung als auch Ausfall löschen)
            Stunde stunde = entityManager.merge(vertretung.getStunde());
            stunde.setIstVertretung(false);
            stunde.setIstAusfall(false);

            // Vertretung löschen
            Vertretung managed = entityManager.merge(vertretung);
            entityManager.remove(managed);

            entityManager.getTransaction().commit();
            entityManager.clear();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new jakarta.persistence.PersistenceException(
                    "Vertretung konnte nicht gelöscht werden.", e
            );
        }
    }
}