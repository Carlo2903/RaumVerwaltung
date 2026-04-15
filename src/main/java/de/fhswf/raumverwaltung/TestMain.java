package de.fhswf.raumverwaltung;

import de.fhswf.raumverwaltung.db.DatabaseConnection;
import de.fhswf.raumverwaltung.db.entities.*;
import jakarta.persistence.EntityManager;
import java.util.Date;

public class TestMain {
    public static void main(String[] args) {
        System.out.println("Starte Datenbankverbindung...");

        EntityManager em = DatabaseConnection.getInstance().getEntityManager();
        System.out.println("Verbindung erfolgreich!");

        em.getTransaction().begin();

        try {
            // 1. Einen Raum erstellen (mit Enum!)
            Raum chemieRaum = new Raum();
            chemieRaum.setBezeichnung("Chemie 1");
            chemieRaum.setRaumtyp(RaumTyp.FACHRAUM_CHEMIE); // Hier nutzen wir das Enum!
            chemieRaum.setKapazitaet(30);
            em.persist(chemieRaum); // Speichern

            // 2. Einen Lehrer erstellen
            Lehrkraft herrMueller = new Lehrkraft();
            herrMueller.setName("Herr Müller");
            herrMueller.setKuerzel("MUE");
            herrMueller.setSollStunden(25);
            em.persist(herrMueller); // Speichern

            // 3. Einen Stundenplan erstellen
            Stundenplan plan = new Stundenplan();
            plan.setGueltigAb(new Date());

            // 4. Eine Unterrichtsstunde erstellen und verknüpfen
            Stunde matheStunde = new Stunde();
            matheStunde.setRaum(chemieRaum);
            matheStunde.setLehrkraft(herrMueller);
            matheStunde.setStundenplan(plan); // WICHTIG: Rückverweis zum Plan!
            matheStunde.setIstAusfall(false);

            // 5. Die Stunde in die Liste des Stundenplans legen
            plan.getStunden().add(matheStunde);

            // 6. Den Stundenplan speichern
            // MAGIE: Wegen CascadeType.ALL in deiner Stundenplan-Klasse
            // wird die 'matheStunde' jetzt völlig automatisch mit in die Datenbank gespeichert!
            em.persist(plan);

            em.getTransaction().commit();
            System.out.println("ERFOLG: Raum, Lehrer, Stundenplan und Stunde wurden gespeichert!");

        } catch (Exception e) {
            em.getTransaction().rollback();
            System.err.println("Fehler beim Speichern: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.getInstance().close();
            System.out.println("Verbindung geschlossen.");
        }
    }
}