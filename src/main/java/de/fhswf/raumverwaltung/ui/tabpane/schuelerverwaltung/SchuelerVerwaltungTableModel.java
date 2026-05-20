package de.fhswf.raumverwaltung.ui.tabpane.schuelerverwaltung;

import de.fhswf.raumverwaltung.db.dao.KlasseDao;
import de.fhswf.raumverwaltung.db.dao.SchuelerDao;
import de.fhswf.raumverwaltung.db.entities.Klasse;
import de.fhswf.raumverwaltung.db.entities.Schueler;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class SchuelerVerwaltungTableModel extends Observable {

    private static SchuelerVerwaltungTableModel instance;

    private final SchuelerDao schuelerDao = new SchuelerDao();
    private final KlasseDao   klasseDao   = new KlasseDao();

    @Getter
    private List<Schueler> schueler    = new ArrayList<>();

    @Getter
    private List<Klasse>   alleKlassen = new ArrayList<>();

    private SchuelerVerwaltungTableModel() {}

    public static SchuelerVerwaltungTableModel getInstance() {
        if (instance == null) {
            instance = new SchuelerVerwaltungTableModel();
        }
        return instance;
    }

    public void loadAll() {
        schuelerDao.clearCache();
        klasseDao.clearCache();
        this.schueler    = schuelerDao.findAll();
        this.alleKlassen = klasseDao.findAll();
        setChanged();
        notifyObservers();
    }

    public void speichern(Schueler s) {
        if (s.getId() == null) {
            schuelerDao.persist(s);
        } else {
            schuelerDao.merge(s);
        }
        loadAll();
    }

    public void loeschen(Schueler s) {
        // Alle Erziehungsberechtigten des Schülers löschen
        de.fhswf.raumverwaltung.db.dao.ErziehungsberechtigterDao eDao = 
                new de.fhswf.raumverwaltung.db.dao.ErziehungsberechtigterDao();
        List<de.fhswf.raumverwaltung.db.entities.Erziehungsberechtigter> eltern = 
                eDao.findeNachSchueler(s);
        eltern.forEach(eDao::remove);

        // Schüler löschen
        schuelerDao.remove(s);
        loadAll();
    }
}