package de.fhswf.raumverwaltung.db.dao;

import de.fhswf.raumverwaltung.db.entities.*;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class StundeDao extends GenericDao<Stunde> {

    /**
     * Sucht nach allen Stunden, die einen Konflikt verursachen könnten.
     */
    public List<Stunde> findeKollisionen(Zeitslot slot, Raum raum, Lehrkraft lehrer, Klasse klasse) {
        String jpql = "SELECT s FROM Stunde s WHERE s.zeitslot = :slot AND " +
                "(s.raum = :raum OR s.lehrkraft = :lehrer OR s.klasse = :klasse)";

        TypedQuery<Stunde> query = entityManager.createQuery(jpql, Stunde.class);
        query.setParameter("slot", slot);
        query.setParameter("raum", raum);
        query.setParameter("lehrer", lehrer);
        query.setParameter("klasse", klasse);

        return query.getResultList();
    }
}