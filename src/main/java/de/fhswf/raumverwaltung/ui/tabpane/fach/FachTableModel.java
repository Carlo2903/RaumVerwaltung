package de.fhswf.raumverwaltung.ui.tabpane.fach;

import de.fhswf.raumverwaltung.db.dao.FachDao;
import de.fhswf.raumverwaltung.db.dao.LehrkraftDao;
import de.fhswf.raumverwaltung.db.entities.Fach;
import de.fhswf.raumverwaltung.db.entities.Klasse;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class FachTableModel extends Observable {

    private static FachTableModel instance;
    private final FachDao dao = new FachDao();
    private final LehrkraftDao lehrkraftDao = new LehrkraftDao();

    private List<Fach> faecher = new ArrayList<>();

    private FachTableModel() {}

    public static FachTableModel getInstance() {
        if (instance == null) {
            instance = new FachTableModel();
        }
        return instance;
    }

    public boolean kannGeloeschtWerden(Fach fach) {
        return !dao.wirdVerwendet(fach);
    }

    public List<Fach> getFaecher() {
        return faecher;
    }

    public void loadAll() {
        dao.clearCache();
        faecher = dao.findAll();
        setChanged();
        notifyObservers();
    }

    public void speichern(Fach fach) {
        if (fach.getId() == null) {
            dao.persist(fach);
        } else {
            dao.merge(fach);
        }
        loadAll();
    }

    public void loeschen(Fach fach) {
        lehrkraftDao.entferneFachAusAllenLehrkraeften(fach);
        dao.remove(fach);
        loadAll();
    }
}