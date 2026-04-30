package de.fhswf.raumverwaltung.db.dao;

import de.fhswf.raumverwaltung.db.entities.Lehrkraft;
import de.fhswf.raumverwaltung.db.entities.Sperrzeit;
import de.fhswf.raumverwaltung.db.entities.Zeitslot;

import java.util.List;

public class SperrzeitDao extends GenericDao<Sperrzeit> {

    public List<Sperrzeit> findeNachLehrkraft(Lehrkraft lehrkraft) {
        clearCache();
        return entityManager
                .createQuery(
                        "SELECT s FROM Sperrzeit s " +
                                "LEFT JOIN FETCH s.zeitslot " +
                                "WHERE s.lehrkraft = :lehrkraft",
                        Sperrzeit.class)
                .setParameter("lehrkraft", lehrkraft)
                .getResultList();
    }

    public void loescheAlleVonLehrkraft(Lehrkraft lehrkraft) {
        entityManager.getTransaction().begin();
        entityManager
                .createQuery(
                        "DELETE FROM Sperrzeit s WHERE s.lehrkraft = :lehrkraft")
                .setParameter("lehrkraft", lehrkraft)
                .executeUpdate();
        entityManager.getTransaction().commit();
        entityManager.clear();
    }
}