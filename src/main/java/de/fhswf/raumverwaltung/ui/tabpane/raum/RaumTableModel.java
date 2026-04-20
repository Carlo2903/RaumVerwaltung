package de.fhswf.raumverwaltung.ui.tabpane.raum;

import de.fhswf.raumverwaltung.db.dao.RaumDao;
import de.fhswf.raumverwaltung.db.entities.Raum;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class RaumTableModel extends Observable {

    private static RaumTableModel instance = null;

    @Getter
    private List<Raum> raeume = new ArrayList<>();
    private final RaumDao raumDao = new RaumDao();

    private RaumTableModel() { loadAll(); }

    public static RaumTableModel getInstance() {
        if (instance == null) instance = new RaumTableModel();
        return instance;
    }

    public boolean kannGeloeschtWerden(Raum raum) {
        return !raumDao.wirdVerwendet(raum);
    }

    public void loadAll() {
        this.raeume = raumDao.findAll();
        setChanged();
        notifyObservers();
    }

    public void speichern(Raum raum) {
        if (raum.getId() == null) {
            raumDao.persist(raum);
        } else {
            raumDao.merge(raum);
        }
        loadAll();
    }

    public void loeschen(Raum raum) {
        raumDao.remove(raum);
        loadAll();
    }
}