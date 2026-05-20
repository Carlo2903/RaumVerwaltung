package de.fhswf.raumverwaltung.db;



import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private EntityManagerFactory emf;
    private EntityManager em;

    private DatabaseConnection() {
        emf = Persistence.createEntityManagerFactory("SchulPlanerPU");
        em = emf.createEntityManager();
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public void close() {
        if (em != null) em.close();
        if (emf != null) emf.close();
    }
}
