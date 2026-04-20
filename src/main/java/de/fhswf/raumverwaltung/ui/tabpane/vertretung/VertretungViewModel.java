package de.fhswf.raumverwaltung.ui.tabpane.vertretung;

import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.db.exception.PlanungException;
import javafx.beans.property.*;
import javafx.collections.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Observable;
import java.util.Observer;

public class VertretungViewModel implements Observer {

    private final VertretungTableModel model;

    @Getter
    private final ObjectProperty<ObservableList<Lehrkraft>> lehrkraefteProperty
            = new SimpleObjectProperty<>(FXCollections.observableArrayList());

    @Getter
    private final ObjectProperty<ObservableList<Stunde>> betroffeneStundenProperty
            = new SimpleObjectProperty<>(FXCollections.observableArrayList());

    @Getter
    private final ObjectProperty<ObservableList<Lehrkraft>> verfuegbareLehrerProperty
            = new SimpleObjectProperty<>(FXCollections.observableArrayList());

    public VertretungViewModel() {
        this.model = VertretungTableModel.getInstance();
        this.model.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        lehrkraefteProperty.set(
                FXCollections.observableList(model.getAlleLehrkraefte())
        );
        betroffeneStundenProperty.set(
                FXCollections.observableList(model.getBetroffeneStunden())
        );
        verfuegbareLehrerProperty.set(
                FXCollections.observableList(model.getVerfuegbareLehrer())
        );
    }

    public void laden() {
        model.laden();
    }

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
}