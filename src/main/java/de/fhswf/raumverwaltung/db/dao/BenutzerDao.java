package de.fhswf.raumverwaltung.db.dao;

import de.fhswf.raumverwaltung.db.entities.Benutzer;
import java.util.Optional;

public class BenutzerDao extends GenericDao<Benutzer> {

    // Login-Suche: Benutzername + Passwort-Hash
    public Optional<Benutzer> findeNachLogin(String benutzername, String passwortHash) {
        return entityManager
                .createQuery(
                        "SELECT b FROM Benutzer b WHERE b.benutzername = :name " +
                                "AND b.passwortHash = :hash AND b.aktiv = true",
                        Benutzer.class)
                .setParameter("name", benutzername)
                .setParameter("hash", passwortHash)
                .getResultStream()
                .findFirst();
    }
}