package de.fhswf.raumverwaltung.ui.tabpane.erziehungsberechtigter;

import de.fhswf.raumverwaltung.db.dao.ErziehungsberechtigterDao;
import de.fhswf.raumverwaltung.db.dao.SchuelerDao;
import de.fhswf.raumverwaltung.db.entities.Erziehungsberechtigter;
import de.fhswf.raumverwaltung.db.entities.Schueler;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class ErziehungsberechtigterTableModel extends Observable {

    private static ErziehungsberechtigterTableModel instance;

    private final ErziehungsberechtigterDao dao        = new ErziehungsberechtigterDao();
    private final SchuelerDao               schuelerDao = new SchuelerDao();

    @Getter
    private List<Erziehungsberechtigter> erziehungsberechtigte = new ArrayList<>();

    @Getter
    private List<Schueler> alleSchueler = new ArrayList<>();

    private ErziehungsberechtigterTableModel() {}

    public static ErziehungsberechtigterTableModel getInstance() {
        if (instance == null) {
            instance = new ErziehungsberechtigterTableModel();
        }
        return instance;
    }

    public void loadAll() {
        dao.clearCache();
        schuelerDao.clearCache();
        this.erziehungsberechtigte = dao.findAll();
        this.alleSchueler          = schuelerDao.findAll();
        setChanged();
        notifyObservers();
    }

    public void speichern(Erziehungsberechtigter e) {
        if (e.getId() == null) {
            dao.persist(e);
        } else {
            dao.merge(e);
        }
        loadAll();
    }

    public void loeschen(Erziehungsberechtigter e) {
        dao.remove(e);
        loadAll();
    }
}
