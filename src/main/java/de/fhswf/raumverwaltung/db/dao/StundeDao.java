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
}