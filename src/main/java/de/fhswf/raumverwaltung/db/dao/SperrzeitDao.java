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

    public List<Sperrzeit> findeNachZeitslot(Zeitslot zeitslot) {
        clearCache();
        return entityManager
                .createQuery(
                        "SELECT s FROM Sperrzeit s " +
                                "LEFT JOIN FETCH s.lehrkraft " +
                                "WHERE s.zeitslot = :zeitslot",
                        Sperrzeit.class)
                .setParameter("zeitslot", zeitslot)
                .getResultList();
    }

    public void speichereAlleVonLehrkraft(Lehrkraft lehrkraft,
                                          List<Zeitslot> zeitslots) {
        try {
            entityManager.getTransaction().begin();

            entityManager
                    .createQuery(
                            "DELETE FROM Sperrzeit s WHERE s.lehrkraft = :lk")
                    .setParameter("lk", lehrkraft)
                    .executeUpdate();

            zeitslots.forEach(zeitslot -> {
                Sperrzeit sz = Sperrzeit.builder()
                        .lehrkraft(lehrkraft)
                        .zeitslot(zeitslot)
                        .build();
                entityManager.persist(sz);
            });

            entityManager.getTransaction().commit();
            entityManager.clear();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new jakarta.persistence.PersistenceException(
                    "Sperrzeiten konnten nicht gespeichert werden.", e
            );
        }
    }
}