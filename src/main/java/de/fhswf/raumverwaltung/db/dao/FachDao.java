package de.fhswf.raumverwaltung.db.dao;

import de.fhswf.raumverwaltung.db.entities.Fach;
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
}