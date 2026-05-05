package de.fhswf.raumverwaltung.ui.tabpane.klasse;

import de.fhswf.raumverwaltung.db.dao.KlasseDao;
import de.fhswf.raumverwaltung.db.entities.Klasse;
import de.fhswf.raumverwaltung.db.entities.Lehrkraft;

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

    public boolean kannGeloeschtWerden(Klasse klasse) {
        return !dao.wirdVerwendet(klasse);
    }

    public List<Klasse> getKlassen() {
        return klassen;
    }

    public void loadAll() {
        dao.clearCache();
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
        // 1. Zuerst den Login-Account der Klasse entfernen
        de.fhswf.raumverwaltung.db.dao.BenutzerDao benutzerDao = new de.fhswf.raumverwaltung.db.dao.BenutzerDao();
        benutzerDao.findeSchuelerBenutzerNachKlasse(klasse)
                .ifPresent(benutzerDao::remove);

        // 2. Dann die Klasse löschen
        dao.remove(klasse);
        loadAll();
    }
}