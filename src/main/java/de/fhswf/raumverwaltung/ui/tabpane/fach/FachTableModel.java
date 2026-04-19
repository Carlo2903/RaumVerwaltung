package de.fhswf.raumverwaltung.ui.tabpane.fach;

import de.fhswf.raumverwaltung.db.dao.FachDao;
import de.fhswf.raumverwaltung.db.entities.Fach;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class FachTableModel extends Observable {

    private static FachTableModel instance;
    private final FachDao dao = new FachDao();
    private List<Fach> faecher = new ArrayList<>();

    private FachTableModel() {}

    public static FachTableModel getInstance() {
        if (instance == null) {
            instance = new FachTableModel();
        }
        return instance;
    }

    public List<Fach> getFaecher() {
        return faecher;
    }

    public void loadAll() {
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
        dao.remove(fach);
        loadAll();
    }
}