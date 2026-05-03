package de.fhswf.raumverwaltung.db.dao;

import de.fhswf.raumverwaltung.db.entities.Erziehungsberechtigter;
import de.fhswf.raumverwaltung.db.entities.Schueler;

import java.util.List;

public class ErziehungsberechtigterDao extends GenericDao<Erziehungsberechtigter> {

    /** Alle Erziehungsberechtigten eines bestimmten Schülers laden. */
    public List<Erziehungsberechtigter> findeNachSchueler(Schueler schueler) {
        return entityManager
                .createQuery(
                        "SELECT e FROM Erziehungsberechtigter e WHERE e.schueler = :schueler",
                        Erziehungsberechtigter.class)
                .setParameter("schueler", schueler)
                .getResultList();
    }
}
