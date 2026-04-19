package de.fhswf.raumverwaltung.db.dao;

import de.fhswf.raumverwaltung.db.entities.Schuljahr;
import java.util.Optional;

public class SchuljahrDao extends GenericDao<Schuljahr> {

    public Optional<Schuljahr> findeAktives() {
        return entityManager
                .createQuery(
                        "SELECT s FROM Schuljahr s WHERE s.istAktiv = true",
                        Schuljahr.class)
                .getResultStream()
                .findFirst();
    }
}