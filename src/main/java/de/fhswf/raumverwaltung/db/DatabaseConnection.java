package de.fhswf.raumverwaltung.db;



import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private EntityManagerFactory emf;
    private EntityManager em;

    // Privater Konstruktor (Singleton-Pattern)
    private DatabaseConnection() {
        // "SchulPlanerPU" MUSS exakt mit dem Namen in deiner persistence.xml übereinstimmen!
        emf = Persistence.createEntityManagerFactory("SchulPlanerPU");
        em = emf.createEntityManager();
    }

    // Holen der einzigen Instanz
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // Holen des EntityManagers für Datenbankabfragen
    public EntityManager getEntityManager() {
        return em;
    }

    // Verbindung sauber schließen
    public void close() {
        if (em != null) em.close();
        if (emf != null) emf.close();
    }
}
