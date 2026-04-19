package de.fhswf.raumverwaltung.db.dao;

import de.fhswf.raumverwaltung.db.entities.Fach;
import java.util.List;

public class FachDao extends GenericDao<Fach> {

    // Fach nach Kürzel suchen
    public Fach findeNachKuerzel(String kuerzel) {
        return entityManager
                .createQuery("SELECT f FROM Fach f WHERE f.kuerzel = :kuerzel", Fach.class)
                .setParameter("kuerzel", kuerzel)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
}