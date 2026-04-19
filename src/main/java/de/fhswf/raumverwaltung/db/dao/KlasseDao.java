package de.fhswf.raumverwaltung.db.dao;

import de.fhswf.raumverwaltung.db.entities.Klasse;
import java.util.List;

public class KlasseDao extends GenericDao<Klasse> {

    // Alle Klassen einer Jahrgangsstufe
    public List<Klasse> findeNachJahrgangsstufe(int jahrgangsstufe) {
        return entityManager
                .createQuery(
                        "SELECT k FROM Klasse k WHERE k.jahrgangsstufe = :stufe ORDER BY k.bezeichnung",
                        Klasse.class)
                .setParameter("stufe", jahrgangsstufe)
                .getResultList();
    }
}