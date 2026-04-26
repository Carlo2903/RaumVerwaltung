package de.fhswf.raumverwaltung.ui.tabpane.vertretung;

import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.db.exception.PlanungException;
import javafx.collections.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Observable;
import java.util.Observer;

public class VertretungViewModel implements Observer {

    private final VertretungTableModel model;

    // ObservableList direkt – kein ObjectProperty<ObservableList>
    @Getter
    private final ObservableList<Lehrkraft> lehrkraefte
            = FXCollections.observableArrayList();

    @Getter
    private final ObservableList<Stunde> betroffeneStunden
            = FXCollections.observableArrayList();

    @Getter
    private final ObservableList<Lehrkraft> verfuegbareLehrer
            = FXCollections.observableArrayList();

    public VertretungViewModel() {
        this.model = VertretungTableModel.getInstance();
        this.model.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        // setAll() feuert ListChangeListener immer zuverlässig
        lehrkraefte.setAll(model.getAlleLehrkraefte());
        betroffeneStunden.setAll(model.getBetroffeneStunden());
        verfuegbareLehrer.setAll(model.getVerfuegbareLehrer());
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

    public Stunde getAusgewaehlteStunde() {
        return model.getAusgewaehlteStunde();
    }
    
    // Vertretungslehrer für eine Stunde
    public String getVertretungslehrerName(Stunde stunde) {
        if (stunde.getId() == null) return "–";
        Vertretung v = model.getVertretungenProStunde().get(stunde.getId());
        return v != null ? v.getVertretungsLehrer().getName() : "–";
    }

}