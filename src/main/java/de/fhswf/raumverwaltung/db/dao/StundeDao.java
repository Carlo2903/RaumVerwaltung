package de.fhswf.raumverwaltung.db.dao;

import de.fhswf.raumverwaltung.db.entities.*;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class StundeDao extends GenericDao<Stunde> {

    // Konfliktprüfung: Gibt es Überschneidungen für diesen Zeitslot?
    public List<Stunde> findeKollisionen(Zeitslot slot, Raum raum,
                                         Lehrkraft lehrer, Klasse klasse) {
        String jpql = "SELECT s FROM Stunde s WHERE s.zeitslot = :slot AND " +
                "(s.raum = :raum OR s.lehrkraft = :lehrer OR s.klasse = :klasse)";

        return entityManager.createQuery(jpql, Stunde.class)
                .setParameter("slot", slot)
                .setParameter("raum", raum)
                .setParameter("lehrer", lehrer)
                .setParameter("klasse", klasse)
                .getResultList();
    }

    // Stunden einer Lehrkraft an bestimmten Wochentagen (für Vertretungsplanung)
    public List<Stunde> findeNachLehrkraftUndWochentage(Lehrkraft lehrkraft,
                                                        List<Wochentag> wochentage) {
        if (wochentage.isEmpty()) return List.of();
        clearCache();
        return entityManager
                .createQuery(
                        "SELECT s FROM Stunde s " +
                                "LEFT JOIN FETCH s.fach " +
                                "LEFT JOIN FETCH s.klasse " +
                                "LEFT JOIN FETCH s.zeitslot " +
                                "WHERE s.lehrkraft = :lk " +
                                "AND s.zeitslot.wochentag IN :tage",
                        Stunde.class)
                .setParameter("lk", lehrkraft)
                .setParameter("tage", wochentage)
                .getResultList();
    }

    public List<Stunde> findeNachStundenplan(Stundenplan stundenplan) {
        clearCache();
        return entityManager
                .createQuery(
                        "SELECT s FROM Stunde s " +
                                "LEFT JOIN FETCH s.fach " +
                                "LEFT JOIN FETCH s.lehrkraft " +
                                "LEFT JOIN FETCH s.raum " +
                                "LEFT JOIN FETCH s.klasse " +
                                "LEFT JOIN FETCH s.zeitslot " +
                                "WHERE s.stundenplan = :plan",
                        Stunde.class)
                .setParameter("plan", stundenplan)
                .getResultList();
    }
    // Alle Stunden eines Zeitslots (für Raumplan-Ansicht)
    public List<Stunde> findeNachZeitslot(Zeitslot zeitslot) {
        return entityManager
                .createQuery(
                        "SELECT s FROM Stunde s WHERE s.zeitslot = :slot",
                        Stunde.class)
                .setParameter("slot", zeitslot)
                .getResultList();
    }

    public List<Stunde> findeNachKlasseUndFach(Klasse klasse, Fach fach) {
        clearCache();
        return entityManager
                .createQuery(
                        "SELECT s FROM Stunde s " +
                                "WHERE s.klasse = :klasse " +
                                "AND s.fach = :fach",
                        Stunde.class)
                .setParameter("klasse", klasse)
                .setParameter("fach", fach)
                .getResultList();
    }

    /**
     * Zählt die belegten Wochenstunden einer Lehrkraft direkt in der Datenbank.
     * Effizienter als findAll().stream().filter().count(), da keine Objekte geladen werden.
     */
    public long zaehleBelegtStunden(Lehrkraft lehrkraft) {
        return entityManager
                .createQuery(
                        "SELECT COUNT(s) FROM Stunde s WHERE s.lehrkraft = :lk",
                        Long.class)
                .setParameter("lk", lehrkraft)
                .getSingleResult();
    }

    /**
     * Löscht eine Stunde inkl. aller zugehörigen Vertretungen in einer Transaktion.
     * Verwendet JPQL-Bulk-DELETE, um Konflikte mit Hibernates orphanRemoval
     * auf Stundenplan.stunden zu umgehen.
     */
    public void loeschenMitVertretungen(Long stundeId) {
        try {
            entityManager.getTransaction().begin();

            // 1. Vertretungen dieser Stunde zuerst löschen (FK-Constraint)
            entityManager.createQuery(
                            "DELETE FROM Vertretung v WHERE v.stunde.id = :sid")
                    .setParameter("sid", stundeId)
                    .executeUpdate();

            // 2. Stunde selbst löschen
            entityManager.createQuery(
                            "DELETE FROM Stunde s WHERE s.id = :sid")
                    .setParameter("sid", stundeId)
                    .executeUpdate();

            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new jakarta.persistence.PersistenceException(
                    "Stunde konnte nicht gelöscht werden.", e);
        }

        // Cache leeren, da Bulk-DELETE den Persistence Context nicht aktualisiert
        entityManager.clear();
    }
}