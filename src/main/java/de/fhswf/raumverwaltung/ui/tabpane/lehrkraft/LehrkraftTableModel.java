package de.fhswf.raumverwaltung.ui.tabpane.lehrkraft;

import de.fhswf.raumverwaltung.db.dao.LehrkraftDao;
import de.fhswf.raumverwaltung.db.entities.Lehrkraft;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class LehrkraftTableModel extends Observable {

    private static LehrkraftTableModel instance;
    private final LehrkraftDao dao = new LehrkraftDao();
    private List<Lehrkraft> lehrkraefte = new ArrayList<>();

    private LehrkraftTableModel() {}

    public static LehrkraftTableModel getInstance() {
        if (instance == null) {
            instance = new LehrkraftTableModel();
        }
        return instance;
    }

    public List<Lehrkraft> getLehrkraefte() {
        return lehrkraefte;
    }

    public void loadAll() {
        lehrkraefte = dao.findAll();
        setChanged();
        notifyObservers();
    }

    public void speichern(Lehrkraft lehrkraft) {
        if (lehrkraft.getId() == null) {
            dao.persist(lehrkraft);
        } else {
            dao.merge(lehrkraft);
        }
        loadAll();
    }

    public void loeschen(Lehrkraft lehrkraft) {
        dao.remove(lehrkraft);
        loadAll();
    }
}