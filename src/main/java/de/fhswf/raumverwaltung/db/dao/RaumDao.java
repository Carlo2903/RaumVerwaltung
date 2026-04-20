package de.fhswf.raumverwaltung.db.dao;

import de.fhswf.raumverwaltung.db.entities.Lehrkraft;
import de.fhswf.raumverwaltung.db.entities.Raum;

public class RaumDao extends GenericDao<Raum>{
    public boolean wirdVerwendet(Raum raum) {
        entityManager.clear();
        Long anzahl = entityManager
                .createQuery(
                        "SELECT COUNT(s) FROM Stunde s WHERE s.raum = :raum",
                        Long.class)
                .setParameter("raum", raum)
                .getSingleResult();
        return anzahl > 0;
    }
}
