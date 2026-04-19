package de.fhswf.raumverwaltung.db.dao;

import de.fhswf.raumverwaltung.db.entities.Abwesenheit;
import de.fhswf.raumverwaltung.db.entities.Lehrkraft;
import java.time.LocalDate;
import java.util.List;

public class AbwesenheitDao extends GenericDao<Abwesenheit> {

    // Alle aktiven Abwesenheiten einer Lehrkraft
    public List<Abwesenheit> findeAktiveNachLehrkraft(Lehrkraft lehrkraft) {
        return entityManager
                .createQuery(
                        "SELECT a FROM Abwesenheit a WHERE a.lehrkraft = :lk AND a.aktiv = true",
                        Abwesenheit.class)
                .setParameter("lk", lehrkraft)
                .getResultList();
    }

    // Abwesenheiten die ein bestimmtes Datum betreffen
    public List<Abwesenheit> findeNachDatum(LocalDate datum) {
        return entityManager
                .createQuery(
                        "SELECT a FROM Abwesenheit a WHERE :datum BETWEEN a.von AND a.bis AND a.aktiv = true",
                        Abwesenheit.class)
                .setParameter("datum", datum)
                .getResultList();
    }
}