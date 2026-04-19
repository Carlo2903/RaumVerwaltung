package de.fhswf.raumverwaltung.ui.tabpane.klasse;

import de.fhswf.raumverwaltung.db.dao.KlasseDao;
import de.fhswf.raumverwaltung.db.entities.Klasse;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class KlasseTableModel extends Observable {

    private static KlasseTableModel instance;
    private final KlasseDao dao = new KlasseDao();
    private List<Klasse> klassen = new ArrayList<>();

    private KlasseTableModel() {}

    public static KlasseTableModel getInstance() {
        if (instance == null) {
            instance = new KlasseTableModel();
        }
        return instance;
    }

    public List<Klasse> getKlassen() {
        return klassen;
    }

    public void loadAll() {
        klassen = dao.findAll();
        setChanged();
        notifyObservers();
    }

    public void speichern(Klasse klasse) {
        if (klasse.getId() == null) {
            dao.persist(klasse);
        } else {
            dao.merge(klasse);
        }
        loadAll();
    }

    public void loeschen(Klasse klasse) {
        dao.remove(klasse);
        loadAll();
    }
}