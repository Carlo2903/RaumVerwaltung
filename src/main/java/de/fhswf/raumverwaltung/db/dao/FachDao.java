package de.fhswf.raumverwaltung.db.dao;

import de.fhswf.raumverwaltung.db.entities.Fach;
import de.fhswf.raumverwaltung.db.entities.Lehrkraft;

import java.util.Optional;

public class FachDao extends GenericDao<Fach> {

    // Konsistent: Optional statt null
    public Optional<Fach> findeNachKuerzel(String kuerzel) {
        return entityManager
                .createQuery(
                        "SELECT f FROM Fach f WHERE f.kuerzel = :kuerzel",
                        Fach.class)
                .setParameter("kuerzel", kuerzel)
                .getResultStream()
                .findFirst();
    }

    public boolean wirdVerwendet(Fach fach) {
        entityManager.clear();

        Long anzahl = entityManager
                .createQuery(
                        "SELECT COUNT(s) FROM Stunde s WHERE s.fach = :fach",
                        Long.class)
                .setParameter("fach", fach)
                .getSingleResult();
        return anzahl > 0;
    }
}