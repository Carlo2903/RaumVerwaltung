package de.fhswf.raumverwaltung.db.dao;

import de.fhswf.raumverwaltung.db.entities.Benutzer;
import java.util.Optional;

public class BenutzerDao extends GenericDao<Benutzer> {

    /**
     * Sucht einen Benutzer anhand seines Benutzernamens und Passwort-Hashes.
     * Wird beim Login verwendet.
     */
    public Optional<Benutzer> findeNachLogin(String benutzername, String passwortHash) {
        return entityManager
                .createQuery(
                        "SELECT b FROM Benutzer b " +
                                "WHERE b.benutzername = :name " +
                                "AND b.passwortHash = :hash " +
                                "AND b.aktiv = true",
                        Benutzer.class)
                .setParameter("name", benutzername)
                .setParameter("hash", passwortHash)
                .getResultStream()
                .findFirst();
    }

    /**
     * Sucht einen Benutzer nur anhand des Benutzernamens (z. B. für Existenzprüfungen).
     */
    public Optional<Benutzer> findeNachBenutzername(String benutzername) {
        return entityManager
                .createQuery(
                        "SELECT b FROM Benutzer b WHERE b.benutzername = :name",
                        Benutzer.class)
                .setParameter("name", benutzername)
                .getResultStream()
                .findFirst();
    }

    /**
     * Sucht den automatisch erstellten Login-Account einer bestimmten Klasse.
     */
    public Optional<de.fhswf.raumverwaltung.db.entities.SchuelerBenutzer> findeSchuelerBenutzerNachKlasse(
            de.fhswf.raumverwaltung.db.entities.Klasse klasse) {
        return entityManager
                .createQuery(
                        "SELECT sb FROM SchuelerBenutzer sb WHERE sb.klasse = :klasse",
                        de.fhswf.raumverwaltung.db.entities.SchuelerBenutzer.class)
                .setParameter("klasse", klasse)
                .getResultStream()
                .findFirst();
    }
}