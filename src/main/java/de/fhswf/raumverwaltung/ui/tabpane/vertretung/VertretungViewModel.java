package de.fhswf.raumverwaltung.ui.tabpane.vertretung;

import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.db.exception.PlanungException;
import de.fhswf.raumverwaltung.ui.util.VertretungUtil;
import javafx.collections.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class VertretungViewModel implements Observer {

    private final VertretungTableModel model;

    @Getter
    private final ObservableList<Lehrkraft> lehrkraefte
            = FXCollections.observableArrayList();

    @Getter
    private final ObservableList<Stunde> betroffeneStunden
            = FXCollections.observableArrayList();

    @Getter
    private final ObservableList<Lehrkraft> verfuegbareLehrer
            = FXCollections.observableArrayList();

    // NEU
    @Getter
    private final ObservableList<AbwesenheitUebersicht> abwesenheitUebersicht
            = FXCollections.observableArrayList();

    public VertretungViewModel() {
        this.model = VertretungTableModel.getInstance();
        this.model.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        lehrkraefte.setAll(model.getAlleLehrkraefte());
        betroffeneStunden.setAll(model.getBetroffeneStunden());
        verfuegbareLehrer.setAll(model.getVerfuegbareLehrer());
        abwesenheitUebersicht.setAll(model.getAbwesenheitUebersicht()); // NEU
    }

    public void laden()                        { model.laden(); }

    public void abwesenheitErfassen(Lehrkraft lehrkraft, LocalDate von,
                                    LocalDate bis, VertretungsGrund grund,
                                    String bemerkung) throws PlanungException {
        model.abwesenheitErfassen(lehrkraft, von, bis, grund, bemerkung);
    }

    public void stundeAuswaehlen(Stunde stunde, LocalDate datum) {
        model.stundeAuswaehlen(stunde, datum);
    }

    public void vertretungZuweisen(Lehrkraft lehrer,
                                   LocalDate datum) throws PlanungException {
        model.vertretungZuweisen(lehrer, datum);
    }

    // NEU
    public void abwesenheitAuswaehlen(AbwesenheitUebersicht uebersicht) {
        model.abwesenheitDirektSetzen(uebersicht.abwesenheit());
    }

    public Stunde getAusgewaehlteStunde() {
        return model.getAusgewaehlteStunde();
    }

    public Map<Long, Vertretung> getVertretungenProStunde() {
        return model.getVertretungenProStunde();
    }

    public String getVertretungslehrerName(Stunde stunde) {
        return VertretungUtil.getVertretungslehrerName(
                stunde, model.getVertretungenProStunde()
        );
    }

    public void loescheVertretung(Stunde stunde) {
        model.loescheVertretung(stunde);
    }

    /**
     * Löscht eine Abwesenheit komplett inkl. aller Vertretungen.
     * Delegation ans Model – keine Logik hier.
     */
    public void abwesenheitLoeschen(Abwesenheit abwesenheit) {
        model.abwesenheitLoeschen(abwesenheit);
    }

    public void setzeAusfall(Stunde stunde, boolean ausfall) {
        model.setzeAusfall(stunde, ausfall);
    }
}