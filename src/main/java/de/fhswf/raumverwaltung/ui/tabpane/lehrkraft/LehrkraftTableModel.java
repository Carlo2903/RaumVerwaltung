package de.fhswf.raumverwaltung.ui.tabpane.lehrkraft;

import de.fhswf.raumverwaltung.db.dao.FachDao;
import de.fhswf.raumverwaltung.db.dao.LehrkraftDao;
import de.fhswf.raumverwaltung.db.dao.SperrzeitDao;
import de.fhswf.raumverwaltung.db.dao.ZeitslotDao;
import de.fhswf.raumverwaltung.db.entities.Lehrkraft;
import de.fhswf.raumverwaltung.db.entities.Raum;
import de.fhswf.raumverwaltung.db.entities.Sperrzeit;
import de.fhswf.raumverwaltung.db.entities.Zeitslot;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class LehrkraftTableModel extends Observable {

    private static LehrkraftTableModel instance;
    private final LehrkraftDao dao = new LehrkraftDao();
    @Getter
    private List<Lehrkraft> lehrkraefte = new ArrayList<>();

    private final SperrzeitDao sperrzeitDao = new SperrzeitDao();
    private final ZeitslotDao  zeitslotDao  = new ZeitslotDao();
    private final FachDao  fachDao  = new FachDao();

    @Getter
    private List<Sperrzeit> aktuelleSperrzeiten = new ArrayList<>();

    @Getter
    private List<Zeitslot> alleZeitslots = new ArrayList<>();




    private LehrkraftTableModel() {}

    public static LehrkraftTableModel getInstance() {
        if (instance == null) {
            instance = new LehrkraftTableModel();
        }
        return instance;
    }

    public boolean kannGeloeschtWerden(Lehrkraft lehrkraft) {
        return !dao.wirdVerwendet(lehrkraft);
    }

    public void loadAll() {
        dao.clearCache();
        fachDao.clearCache();     // falls vorhanden
        zeitslotDao.clearCache();
        this.lehrkraefte   = dao.findAll();
        this.alleZeitslots = zeitslotDao.findAll();
        setChanged();
        notifyObservers("RELOAD");
    }

    public void ladeSperrzeiten(Lehrkraft lehrkraft) {
        if (lehrkraft == null) {
            aktuelleSperrzeiten = new ArrayList<>();
        } else {
            aktuelleSperrzeiten = sperrzeitDao.findeNachLehrkraft(lehrkraft);
        }
        setChanged();
        notifyObservers("SPERRZEITEN");
    }

    public void speichereSperrzeiten(Lehrkraft lehrkraft,
                                     List<Zeitslot> gesperrteZeitslots) {
        sperrzeitDao.speichereAlleVonLehrkraft(lehrkraft, gesperrteZeitslots);
        ladeSperrzeiten(lehrkraft);
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
        // 1. Sperrzeiten löschen
        List<Sperrzeit> sperrzeiten = sperrzeitDao.findeNachLehrkraft(lehrkraft);
        sperrzeiten.forEach(sperrzeitDao::remove);

        // 2. Klassenlehrer-Zuweisungen aufheben
        de.fhswf.raumverwaltung.db.dao.KlasseDao klasseDao = new de.fhswf.raumverwaltung.db.dao.KlasseDao();
        List<de.fhswf.raumverwaltung.db.entities.Klasse> klassen = klasseDao.findAll();
        for (de.fhswf.raumverwaltung.db.entities.Klasse k : klassen) {
            if (k.getKlassenLehrer() != null && k.getKlassenLehrer().getId().equals(lehrkraft.getId())) {
                k.setKlassenLehrer(null);
                klasseDao.merge(k);
            }
        }

        // 3. Benutzer-Account löschen
        de.fhswf.raumverwaltung.db.dao.BenutzerDao benutzerDao = new de.fhswf.raumverwaltung.db.dao.BenutzerDao();
        benutzerDao.findeNachBenutzername(lehrkraft.getKuerzel().toLowerCase())
                .ifPresent(benutzerDao::remove);

        // 4. Lehrkraft löschen
        dao.remove(lehrkraft);
        loadAll();
    }
}