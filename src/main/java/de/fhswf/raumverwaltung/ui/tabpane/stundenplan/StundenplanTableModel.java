package de.fhswf.raumverwaltung.ui.tabpane.stundenplan;

import de.fhswf.raumverwaltung.db.dao.*;
import de.fhswf.raumverwaltung.db.entities.*;
import lombok.Getter;

import java.util.*;

public class StundenplanTableModel extends Observable {

    private static StundenplanTableModel instance;

    private final StundenplanDao stundenplanDao = new StundenplanDao();
    private final SchuljahrDao   schuljahrDao   = new SchuljahrDao();
    private final StundeDao      stundeDao      = new StundeDao();
    private final KlasseDao      klasseDao      = new KlasseDao();
    private final LehrkraftDao lehrkraftDao = new LehrkraftDao();
    private final FachDao      fachDao      = new FachDao();
    private final RaumDao      raumDao      = new RaumDao();

    // Aktuell angezeigter Stundenplan
    @Getter
    private Stundenplan aktuellerPlan = null;

    // Aktuell gefilterte Klasse (null = alle)
    @Getter
    private Klasse aktuelleKlasse = null;

    // Alle Klassen für die FilterComboBox
    @Getter
    private List<Klasse> alleKlassen;

    @Getter
    private List<Lehrkraft> alleLehrkraefte = new ArrayList<>();

    @Getter
    private List<Fach> alleFaecher = new ArrayList<>();

    @Getter
    private List<Raum> alleRaeume = new ArrayList<>();

    // Das Grid: Wochentag → Stundennummer → Stunde
    // Beispiel: grid.get(MONTAG).get(1) = die 1. Stunde am Montag
    @Getter
    private Map<Wochentag, Map<Integer, Stunde>> stundenGrid = new HashMap<>();

    @Getter
    private List<Zeitslot> alleZeitslots = new ArrayList<>();

    private final ZeitslotDao zeitslotDao = new ZeitslotDao();

    private StundenplanTableModel() {}

    public static StundenplanTableModel getInstance() {
        if (instance == null) {
            instance = new StundenplanTableModel();
        }
        return instance;
    }

    // Initialer Ladevorgang
    public void laden() {
        alleKlassen = klasseDao.findAll();
        alleLehrkraefte = lehrkraftDao.findAll();
        alleFaecher     = fachDao.findAll();
        alleRaeume      = raumDao.findAll();
        schuljahrDao.clearCache();
        stundeDao.clearCache();
        alleZeitslots   = zeitslotDao.findAll();

        // Aktives Schuljahr suchen
        Optional<Schuljahr> schuljahr = schuljahrDao.findeAktives();
        if (schuljahr.isEmpty()) {
            aktuellerPlan = null;
            stundenGrid   = new HashMap<>();
            setChanged();
            notifyObservers();
            return;
        }

        // Ersten Plan des aktiven Schuljahres laden
        List<Stundenplan> plaene = stundenplanDao.findeNachSchuljahr(schuljahr.get());
        if (plaene.isEmpty()) {
            aktuellerPlan = null;
            stundenGrid   = new HashMap<>();
            setChanged();
            notifyObservers();
            return;
        }

        aktuellerPlan = plaene.get(0);
        bauGrid();
    }

    // Klassen-Filter setzen und Grid neu aufbauen
    public void filterNachKlasse(Klasse klasse) {
        this.aktuelleKlasse = klasse;
        bauGrid();
    }

    // Stunde speichern und Grid aktualisieren
    public void stundeSetzen(Stunde stunde) {
        if (stunde.getId() == null) {
            stundeDao.persist(stunde);
        } else {
            stundeDao.merge(stunde);
        }
        bauGrid();
    }

    // Stunde löschen
    public void stundeLoeschen(Stunde stunde) {
        stundeDao.remove(stunde);
        bauGrid();
    }

    // Grid aus den Stunden des aktiven Plans aufbauen
    private void bauGrid() {
        stundenGrid = new HashMap<>();

        // Alle Wochentage initialisieren
        for (Wochentag tag : Wochentag.values()) {
            stundenGrid.put(tag, new HashMap<>());
        }

        if (aktuellerPlan == null) {
            setChanged();
            notifyObservers();
            return;
        }

        // Stunden filtern (nach Klasse falls gesetzt) und ins Grid eintragen
        aktuellerPlan.getStunden().stream()
                .filter(s -> aktuelleKlasse == null
                        || s.getKlasse().equals(aktuelleKlasse))
                .forEach(s -> {
                    Wochentag tag    = s.getZeitslot().getWochentag();
                    int stundeNummer = s.getZeitslot().getStundenNummer();
                    stundenGrid.get(tag).put(stundeNummer, s);
                });

        setChanged();
        notifyObservers();
    }
}