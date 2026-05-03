package de.fhswf.raumverwaltung.db.dao;

import de.fhswf.raumverwaltung.db.entities.Abwesenheit;
import de.fhswf.raumverwaltung.db.entities.Lehrkraft;

import java.time.LocalDate;
import java.util.List;

public class AbwesenheitDao extends GenericDao<Abwesenheit> {

    public List<Abwesenheit> findeAktiveNachLehrkraft(Lehrkraft lehrkraft) {
        return entityManager
                .createQuery(
                        "SELECT a FROM Abwesenheit a " +
                                "WHERE a.lehrkraft = :lk AND a.aktiv = true",
                        Abwesenheit.class)
                .setParameter("lk", lehrkraft)
                .getResultList();
    }

    public List<Abwesenheit> findeNachDatum(LocalDate datum) {
        return entityManager
                .createQuery(
                        "SELECT a FROM Abwesenheit a " +
                                "WHERE :datum BETWEEN a.von AND a.bis " +
                                "AND a.aktiv = true",
                        Abwesenheit.class)
                .setParameter("datum", datum)
                .getResultList();
    }

    // NEU
    public List<Abwesenheit> findeAlle() {
        clearCache();
        return entityManager
                .createQuery(
                        "SELECT a FROM Abwesenheit a " +
                                "LEFT JOIN FETCH a.lehrkraft " +
                                "WHERE a.aktiv = true " +
                                "ORDER BY a.von DESC",
                        Abwesenheit.class)
                .getResultList();
    }

    public List<Abwesenheit> findeAktuelleUndZukuenftige() {
        clearCache();
        LocalDate zweiWochenZurueck = LocalDate.now().minusWeeks(2);
        return entityManager
                .createQuery(
                        "SELECT a FROM Abwesenheit a " +
                                "LEFT JOIN FETCH a.lehrkraft " +
                                "WHERE a.aktiv = true " +
                                "AND a.bis >= :grenze " +
                                "ORDER BY a.von DESC",
                        Abwesenheit.class)
                .setParameter("grenze", zweiWochenZurueck)
                .getResultList();
    }
}