package de.fhswf.raumverwaltung.ui.tabpane.vertretung;

import de.fhswf.raumverwaltung.db.dao.AbwesenheitDao;
import de.fhswf.raumverwaltung.db.dao.LehrkraftDao;
import de.fhswf.raumverwaltung.db.dao.VertretungDao;
import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.db.exception.PlanungException;
import de.fhswf.raumverwaltung.service.VertretungsService;
import lombok.Getter;

import java.time.LocalDate;
import java.util.*;

public class VertretungTableModel extends Observable {

    private static VertretungTableModel instance;

    private final VertretungsService vertretungsService = VertretungsService.getInstance();
    private final LehrkraftDao       lehrkraftDao       = new LehrkraftDao();
    private final AbwesenheitDao     abwesenheitDao     = new AbwesenheitDao();
    private final VertretungDao      vertretungDao      = new VertretungDao();

    @Getter
    private List<Lehrkraft> alleLehrkraefte = new ArrayList<>();

    @Getter
    private Abwesenheit aktuelleAbwesenheit = null;

    @Getter
    private List<Stunde> betroffeneStunden = new ArrayList<>();

    @Getter
    private Stunde ausgewaehlteStunde = null;

    @Getter
    private List<Lehrkraft> verfuegbareLehrer = new ArrayList<>();

    @Getter
    private Map<Long, Vertretung> vertretungenProStunde = new HashMap<>();

    @Getter
    private List<AbwesenheitUebersicht> abwesenheitUebersicht = new ArrayList<>();

    private VertretungTableModel() {}

    public static VertretungTableModel getInstance() {
        if (instance == null) {
            instance = new VertretungTableModel();
        }
        return instance;
    }

    public void laden() {
        alleLehrkraefte = lehrkraftDao.findAll();
        ladeUebersicht();
        setChanged();
        notifyObservers();
    }

    public void abwesenheitErfassen(Lehrkraft lehrkraft, LocalDate von,
                                    LocalDate bis, VertretungsGrund grund,
                                    String bemerkung) throws PlanungException {
        aktuelleAbwesenheit = vertretungsService.erfasseAbwesenheit(
                lehrkraft, von, bis, grund, bemerkung
        );
        betroffeneStunden = vertretungsService.findeBetroffeneStunden(
                aktuelleAbwesenheit
        );
        ausgewaehlteStunde = null;
        verfuegbareLehrer  = new ArrayList<>();
        ladeVertretungen();
        ladeUebersicht();
        setChanged();
        notifyObservers();
    }

    public void stundeAuswaehlen(Stunde stunde, LocalDate abwesenheitVon) {
        ausgewaehlteStunde = stunde;

        LocalDate korrekteDatum = vertretungsService.berechneStundenDatum(
                stunde, abwesenheitVon
        );

        verfuegbareLehrer = vertretungsService.findeVertretungskandidaten(
                stunde.getZeitslot(), korrekteDatum
        );
        setChanged();
        notifyObservers();
    }

    public void vertretungZuweisen(Lehrkraft vertretungsLehrer,
                                   LocalDate abwesenheitVon) throws PlanungException {
        if (ausgewaehlteStunde == null) return;

        LocalDate korrekteDatum = vertretungsService.berechneStundenDatum(
                ausgewaehlteStunde, abwesenheitVon
        );

        vertretungsService.weiseVertretungZu(
                ausgewaehlteStunde, vertretungsLehrer,
                korrekteDatum,
                aktuelleAbwesenheit.getGrund(),
                aktuelleAbwesenheit.getBemerkung()
        );

        betroffeneStunden = vertretungsService.findeBetroffeneStunden(
                aktuelleAbwesenheit
        );
        ladeVertretungen();
        ladeUebersicht();
        ausgewaehlteStunde = null;
        verfuegbareLehrer  = new ArrayList<>();
        setChanged();
        notifyObservers();
    }

    public void abwesenheitDirektSetzen(Abwesenheit abwesenheit) {
        aktuelleAbwesenheit = abwesenheit;
        betroffeneStunden   = vertretungsService.findeBetroffeneStunden(abwesenheit);
        ausgewaehlteStunde  = null;
        verfuegbareLehrer   = new ArrayList<>();
        ladeVertretungen();
        setChanged();
        notifyObservers();
    }

    private void ladeVertretungen() {
        vertretungenProStunde = new HashMap<>();
        if (betroffeneStunden.isEmpty()) return;

        vertretungDao.findeNachStunden(betroffeneStunden)
                .forEach(v -> vertretungenProStunde.put(v.getStunde().getId(), v));
    }

    private void ladeUebersicht() {
        List<Abwesenheit> abwesenheiten =
                abwesenheitDao.findeAktuelleUndZukuenftige();

        List<Vertretung> alleVertretungen = vertretungDao.findeAlleAktiven();

        abwesenheitUebersicht = abwesenheiten.stream()
                .map(a -> {
                    List<Stunde> betroffene =
                            vertretungsService.findeBetroffeneStunden(a);

                    long zugewiesen = alleVertretungen.stream()
                            .filter(v -> betroffene.stream()
                                    .anyMatch(s -> s.getId().equals(
                                            v.getStunde().getId())))
                            .count();

                    return new AbwesenheitUebersicht(
                            a,
                            betroffene.size(),
                            (int) zugewiesen // zugewiesen beinhaltet nun auch Ausfälle, da beides als Vertretung-Eintrag existiert
                    );
                })
                .toList();
    }

    public void loescheVertretung(Stunde stunde) {
        vertretungDao.findeNachStunde(stunde).ifPresent(v -> {
            vertretungsService.loescheVertretung(v);
            refreshNachAenderung();
        });
    }

    public void setzeAusfall(Stunde stunde, boolean ausfall) {
        if (ausfall) {
            LocalDate korrekteDatum = vertretungsService.berechneStundenDatum(
                    stunde, aktuelleAbwesenheit.getVon()
            );
            try {
                vertretungsService.weiseAusfallZu(
                        stunde, korrekteDatum, 
                        aktuelleAbwesenheit.getGrund(), 
                        aktuelleAbwesenheit.getBemerkung()
                );
                refreshNachAenderung();
            } catch (PlanungException e) {
                // Exception wird hier bewusst ignoriert – Ausfall ist optional
            }
        } else {
            loescheVertretung(stunde);
        }
    }

    private void refreshNachAenderung() {
        if (aktuelleAbwesenheit != null) {
            betroffeneStunden = vertretungsService
                    .findeBetroffeneStunden(aktuelleAbwesenheit);
        }
        ladeVertretungen();
        ladeUebersicht();
        setChanged();
        notifyObservers();
    }

    /**
     * Löscht eine Abwesenheit vollständig (inkl. aller Vertretungen).
     * Setzt den Zustand zurück und aktualisiert die Übersicht.
     */
    public void abwesenheitLoeschen(Abwesenheit abwesenheit) {
        vertretungsService.loescheAbwesenheitKomplett(abwesenheit);

        // Zustand zurücksetzen falls die gelöschte Abwesenheit aktiv war
        if (aktuelleAbwesenheit != null && abwesenheit.getId().equals(aktuelleAbwesenheit.getId())) {
            aktuelleAbwesenheit = null;
            betroffeneStunden   = new ArrayList<>();
            ausgewaehlteStunde  = null;
            verfuegbareLehrer   = new ArrayList<>();
            vertretungenProStunde = new HashMap<>();
        }

        ladeUebersicht();
        setChanged();
        notifyObservers();
    }

}