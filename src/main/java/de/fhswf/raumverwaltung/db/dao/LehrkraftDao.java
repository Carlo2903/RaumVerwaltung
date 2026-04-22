package de.fhswf.raumverwaltung.db.dao;

import de.fhswf.raumverwaltung.db.entities.Fach;
import de.fhswf.raumverwaltung.db.entities.Lehrkraft;

import java.util.List;

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

    @Override
    public List<Lehrkraft> findAll() {
        entityManager.clear();
        return entityManager
                .createQuery(
                        "SELECT DISTINCT l FROM Lehrkraft l " +
                                "LEFT JOIN FETCH l.faecher",
                        Lehrkraft.class)
                .getResultList();
    }

    public void entferneFachAusAllenLehrkraeften(Fach fach) {
        entityManager.clear();
        // Alle Lehrkräfte die dieses Fach haben laden
        List<Lehrkraft> betroffene = entityManager
                .createQuery(
                        "SELECT l FROM Lehrkraft l WHERE :fach MEMBER OF l.faecher",
                        Lehrkraft.class)
                .setParameter("fach", fach)
                .getResultList();

        // Fach aus jeder Lehrkraft entfernen
        entityManager.getTransaction().begin();
        betroffene.forEach(l -> l.getFaecher().remove(fach));
        entityManager.getTransaction().commit();
    }


    @Override
    public void persist(Lehrkraft entity) {
        try {
            entityManager.getTransaction().begin();

            // Fächer in aktuelle Session einbinden – sonst sind sie detached
            List<Fach> verwaltete = entity.getFaecher().stream()
                    .map(f -> entityManager.merge(f))
                    .toList();
            entity.getFaecher().clear();
            entity.getFaecher().addAll(verwaltete);

            entityManager.persist(entity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new jakarta.persistence.PersistenceException(e);
        }
    }

    @Override
    public Lehrkraft merge(Lehrkraft entity) {
        try {
            entityManager.getTransaction().begin();

            // Fächer auch beim Update in Session einbinden
            List<Fach> verwaltete = entity.getFaecher().stream()
                    .map(f -> entityManager.merge(f))
                    .toList();
            entity.getFaecher().clear();
            entity.getFaecher().addAll(verwaltete);

            Lehrkraft merged = entityManager.merge(entity);
            entityManager.getTransaction().commit();
            return merged;
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new jakarta.persistence.PersistenceException(e);
        }
    }
}