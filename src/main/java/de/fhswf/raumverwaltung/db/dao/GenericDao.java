package de.fhswf.raumverwaltung.db.dao;

import de.fhswf.raumverwaltung.db.DatabaseConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class GenericDao<T> implements Dao<T> {
    private final Class<T> persistentClass;
    protected EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public GenericDao() {
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        this.entityManager = DatabaseConnection.getInstance().getEntityManager();
    }

    @Override
    public void persist(T entity) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(entity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new PersistenceException(e);
        }
    }
    public void clearCache() {
        entityManager.clear();
    }
    @Override
    public T merge(T entity) {
        try {
            entityManager.getTransaction().begin();
            T mergedEntity = entityManager.merge(entity);
            entityManager.getTransaction().commit();
            return mergedEntity;
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new PersistenceException(e);
        }
    }

    @Override
    public void remove(T entity) {
        try {
            entityManager.getTransaction().begin();
            entityManager.remove(
                    entityManager.contains(entity)
                            ? entity
                            : entityManager.merge(entity)
            );
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            // PlanungException statt roher PersistenceException
            throw new jakarta.persistence.PersistenceException(
                    "Datensatz kann nicht gelöscht werden – " +
                            "er wird noch von anderen Einträgen verwendet.", e
            );
        }
    }

    @Override
    public T findById(Object id) {
        return entityManager.find(persistentClass, id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        return entityManager
                .createQuery(
                        "Select t from " + persistentClass.getSimpleName() + " t")
                .getResultList();
    }

}