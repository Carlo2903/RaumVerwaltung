package de.fhswf.raumverwaltung;

import de.fhswf.raumverwaltung.db.DatabaseConnection;
import de.fhswf.raumverwaltung.db.dao.SchuljahrDao;
import de.fhswf.raumverwaltung.db.dao.StundenplanDao;
import de.fhswf.raumverwaltung.db.entities.*;
import jakarta.persistence.EntityManager;
import java.util.Date;

public class TestMain {
    public static void main(String[] args) {
        System.out.println("Starte Datenbankverbindung...");

        EntityManager em = DatabaseConnection.getInstance().getEntityManager();
        System.out.println("Verbindung erfolgreich!");

        em.getTransaction().begin();
    }

            // Schuljahr einmalig erstellen//
        private void erstelleStandardSchuljahr() {
            SchuljahrDao schuljahrDao = new SchuljahrDao();

            // Nur anlegen falls noch kein aktives Schuljahr existiert
            if (schuljahrDao.findeAktives().isPresent()) return;

            Schuljahr schuljahr = Schuljahr.builder()
                    .bezeichnung("2025/2026")
                    .startdatum(java.time.LocalDate.of(2025, 8, 1))
                    .enddatum(java.time.LocalDate.of(2026, 7, 31))
                    .istAktiv(true)
                    .build();
            schuljahrDao.persist(schuljahr);

            // Dazugehörigen Stundenplan anlegen
            StundenplanDao stundenplanDao = new StundenplanDao();
            Stundenplan stundenplan = new Stundenplan();
            stundenplan.setGueltigAb(java.time.LocalDate.of(2025, 8, 1));
            stundenplan.setSchuljahr(schuljahr);
            stundenplanDao.persist(stundenplan);

            System.out.println("Schuljahr 2025/2026 + Stundenplan angelegt.");
        }
}