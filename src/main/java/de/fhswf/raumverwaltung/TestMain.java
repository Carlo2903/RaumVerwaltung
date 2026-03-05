package de.fhswf.raumverwaltung;

import de.fhswf.raumverwaltung.db.DatabaseConnection;
import jakarta.persistence.EntityManager;
import de.fhswf.raumverwaltung.db.entities.Raum;

public class TestMain {
    public static void main(String[] args) {
        System.out.println("Starte Datenbankverbindung...");

        // 1. Verbindung aufbauen (Hier legt Hibernate automatisch die Tabellen an!)
        EntityManager em = DatabaseConnection.getInstance().getEntityManager();
        System.out.println("Verbindung erfolgreich! Tabellen sollten jetzt in pgAdmin sichtbar sein.");

        // 2. Testweise einen Raum speichern
        em.getTransaction().begin();

        Raum bioRaum = new Raum();
        bioRaum.setBezeichnung("Bio 1");
        bioRaum.setTyp("Bio-Fachraum");
        bioRaum.setKapazitaet(30);

        em.persist(bioRaum); // Speichert den Raum in die Datenbank

        em.getTransaction().commit();
        System.out.println("Test-Raum wurde gespeichert!");

        // 3. Verbindung schließen
        DatabaseConnection.getInstance().close();
    }
}
