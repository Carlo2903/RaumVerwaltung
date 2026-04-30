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

    @Getter
    private Map<String, Integer> stundenZaehler = new HashMap<>();

    @Getter
    private Map<Long, Vertretung> vertretungenProStunde = new HashMap<>();

    private final VertretungDao vertretungDao = new VertretungDao();

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
        zeitslotDao.clearCache();
        stundenplanDao.clearCache();
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
        if (stunde == null || stunde.getId() == null) return;

        stundeDao.loeschenMitVertretungen(stunde.getId());
        bauGrid();
    }

    // Grid aus den Stunden des aktiven Plans aufbauen
    private void bauGrid() {
        stundenGrid = new HashMap<>();
        stundenZaehler = new HashMap<>();
        vertretungenProStunde = new HashMap<>();

        // Alle Wochentage initialisieren
        for (Wochentag tag : Wochentag.values()) {
            stundenGrid.put(tag, new HashMap<>());
        }

        if (aktuellerPlan == null) {
            setChanged();
            notifyObservers();
            return;
        }

        //Cache leeren damit neue Stunde auch gefunden wird
        stundeDao.clearCache();
        List<Stunde> aktuelleStunden = stundeDao.findeNachStundenplan(aktuellerPlan);


        vertretungDao.findeNachStundenplan(aktuellerPlan)
                .forEach(v -> vertretungenProStunde.put(v.getStunde().getId(), v));

        // Zähler für alle Klasse+Fach-Kombinationen vorberechnen
        aktuelleStunden.forEach(s -> {
            if (s.getKlasse() != null && s.getFach() != null) {
                String key = s.getKlasse().getId() + "_" + s.getFach().getId();
                stundenZaehler.merge(key, 1, Integer::sum);
            }
        });

        // Stunden filtern (nach id falls gesetzt) und ins Grid eintragen
        aktuelleStunden.stream()
                .filter(s -> aktuelleKlasse == null
                        || (s.getKlasse() != null &&
                        s.getKlasse().getId().equals(aktuelleKlasse.getId())))
                .forEach(s -> {
                    Wochentag tag    = s.getZeitslot().getWochentag();
                    int stundeNummer = s.getZeitslot().getStundenNummer();
                    stundenGrid.get(tag).put(stundeNummer, s);
                });

        setChanged();
        notifyObservers();
    }


    public Optional<Zeitslot> findeZeitslot(Wochentag tag, int stundenNummer) {
        // Direkt aus DB – nicht aus gecachter Liste
        return zeitslotDao.findeNachTagUndNummer(tag, stundenNummer);
    }
}