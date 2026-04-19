package de.fhswf.raumverwaltung.db.dao;

import de.fhswf.raumverwaltung.db.entities.Schuljahr;
import de.fhswf.raumverwaltung.db.entities.Stundenplan;
import java.util.List;
import java.util.Optional;

public class StundenplanDao extends GenericDao<Stundenplan> {

    // Alle Stundenpläne eines Schuljahres
    public List<Stundenplan> findeNachSchuljahr(Schuljahr schuljahr) {
        return entityManager
                .createQuery(
                        "SELECT s FROM Stundenplan s WHERE s.schuljahr = :sj",
                        Stundenplan.class)
                .setParameter("sj", schuljahr)
                .getResultList();
    }
}