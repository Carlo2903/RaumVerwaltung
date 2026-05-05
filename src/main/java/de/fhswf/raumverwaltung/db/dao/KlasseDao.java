package de.fhswf.raumverwaltung.db.dao;

import de.fhswf.raumverwaltung.db.entities.Klasse;
import de.fhswf.raumverwaltung.db.entities.Raum;

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

    public boolean wirdVerwendet(Klasse klasse) {
        entityManager.clear();

        Long stunden = entityManager
                .createQuery(
                        "SELECT COUNT(s) FROM Stunde s WHERE s.klasse = :klasse",
                        Long.class)
                .setParameter("klasse", klasse)
                .getSingleResult();

        Long schueler = entityManager
                .createQuery(
                        "SELECT COUNT(s) FROM Schueler s WHERE s.klasse = :klasse",
                        Long.class)
                .setParameter("klasse", klasse)
                .getSingleResult();

        return stunden > 0 || schueler > 0;
    }
}