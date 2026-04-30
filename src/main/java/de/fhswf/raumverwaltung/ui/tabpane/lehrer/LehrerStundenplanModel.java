package de.fhswf.raumverwaltung.ui.tabpane.lehrer;

import de.fhswf.raumverwaltung.db.dao.*;
import de.fhswf.raumverwaltung.db.entities.*;
import lombok.Getter;

import java.util.*;

public class LehrerStundenplanModel extends Observable {

    // KEIN Singleton – neue Instanz pro Lehrer
    private final Lehrkraft  eigeneLehrkraft;

    private final StundenplanDao stundenplanDao = new StundenplanDao();
    private final SchuljahrDao   schuljahrDao   = new SchuljahrDao();
    private final StundeDao      stundeDao      = new StundeDao();
    private final KlasseDao      klasseDao      = new KlasseDao();
    private final VertretungDao  vertretungDao  = new VertretungDao();

    @Getter private Stundenplan aktuellerPlan  = null;
    @Getter private Klasse      aktuelleKlasse = null;
    @Getter private List<Klasse> alleKlassen   = new ArrayList<>();

    @Getter
    private Map<Wochentag, Map<Integer, Stunde>> stundenGrid = new HashMap<>();

    @Getter
    private Map<String, Integer> stundenZaehler = new HashMap<>();

    @Getter
    private Map<Long, Vertretung> vertretungenProStunde = new HashMap<>();

    @Getter
    private List<Klasse> eigeneKlassen = new ArrayList<>();

    @Getter
    private Map<Long, Integer> lehrkraftStunden = new HashMap<>();


    public LehrerStundenplanModel(Lehrkraft lehrkraft) {
        this.eigeneLehrkraft = lehrkraft;
    }

    public void laden() {
        alleKlassen = klasseDao.findAll();

        schuljahrDao.clearCache();
        stundeDao.clearCache();
        stundenplanDao.clearCache();

        Optional<Schuljahr> schuljahr = schuljahrDao.findeAktives();
        if (schuljahr.isEmpty()) {
            aktuellerPlan = null;
            setChanged();
            notifyObservers();
            return;
        }

        List<Stundenplan> plaene = stundenplanDao.findeNachSchuljahr(schuljahr.get());
        if (plaene.isEmpty()) {
            aktuellerPlan = null;
            setChanged();
            notifyObservers();
            return;
        }

        aktuellerPlan = plaene.get(0);
        bauGrid();
    }

    public void filterNachKlasse(Klasse klasse) {
        this.aktuelleKlasse = klasse;
        bauGrid();
    }

    private void bauGrid() {
        stundenGrid          = new HashMap<>();
        stundenZaehler       = new HashMap<>();
        vertretungenProStunde = new HashMap<>();
        lehrkraftStunden      = new HashMap<>();

        for (Wochentag tag : Wochentag.values()) {
            stundenGrid.put(tag, new HashMap<>());
        }

        if (aktuellerPlan == null) {
            setChanged();
            notifyObservers();
            return;
        }

        stundeDao.clearCache();
        List<Stunde> alleStunden = stundeDao.findeNachStundenplan(aktuellerPlan);

        vertretungDao.findeNachStundenplan(aktuellerPlan)
                .forEach(v -> vertretungenProStunde.put(v.getStunde().getId(), v));

        alleStunden.forEach(s -> {
            if (s.getKlasse() != null && s.getFach() != null) {
                String key = s.getKlasse().getId() + "_" + s.getFach().getId();
                stundenZaehler.merge(key, 1, Integer::sum);
            }
        });

        alleStunden.forEach(s -> {
            if (s.getLehrkraft() != null) {
                lehrkraftStunden.merge(s.getLehrkraft().getId(), 1, Integer::sum);
            }
        });

        //Klasse gewählt → alle Stunden dieser Klasse
        //      Keine Klasse → nur eigene Stunden des Lehrers
        alleStunden.stream()
                .filter(s -> aktuelleKlasse != null
                        ? (s.getKlasse() != null &&
                        s.getKlasse().getId().equals(aktuelleKlasse.getId()))
                        : (s.getLehrkraft() != null &&
                        s.getLehrkraft().getId().equals(eigeneLehrkraft.getId())))
                .forEach(s -> {
                    Wochentag tag    = s.getZeitslot().getWochentag();
                    int stundeNummer = s.getZeitslot().getStundenNummer();
                    stundenGrid.get(tag).put(stundeNummer, s);
                });

        setChanged();
        notifyObservers();
    }
}