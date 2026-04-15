package de.fhswf.raumverwaltung.db.dao;

import java.util.List;

public interface Dao<T> {
    void persist(T entity);
    void remove(T entity);
    T merge(T entity);
    T findById(Object id); // Geändert auf Object für flexiblere IDs
    List<T> findAll();
}