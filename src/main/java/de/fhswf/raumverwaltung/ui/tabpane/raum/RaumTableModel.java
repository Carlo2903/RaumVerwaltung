package de.fhswf.raumverwaltung.ui.tabpane.raum;

import de.fhswf.raumverwaltung.db.dao.RaumDao; // Musst du noch erstellen (erbt von GenericDao)
import de.fhswf.raumverwaltung.db.entities.Raum;
import java.util.*;

public class RaumTableModel extends java.util.Observable {
    private static RaumTableModel instance = null;
    private List<RaumDao> raeume = new ArrayList<>();
    private final RaumDao raumDao = new RaumDao();

    private RaumTableModel() { loadAll(); }

    public static RaumTableModel getInstance() {
        if (instance == null) instance = new RaumTableModel();
        return instance;
    }

    public void loadAll() {
        this.raeume = raumDao.findAll();
        setChanged();
        notifyObservers();
    }

    public List<RaumDao> getRaeume() { return raeume; }
}