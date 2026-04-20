package de.fhswf.raumverwaltung.db.dao;

import de.fhswf.raumverwaltung.db.entities.Lehrkraft;

public class LehrkraftDao extends GenericDao<Lehrkraft> {
    public boolean wirdVerwendet(Lehrkraft lehrkraft) {
        entityManager.clear();

        Long anzahl = entityManager
                .createQuery(
                        "SELECT COUNT(s) FROM Stunde s WHERE s.lehrkraft = :lehrkraft",
                        Long.class)
                .setParameter("lehrkraft", lehrkraft)
                .getSingleResult();
        return anzahl > 0;
    }

}