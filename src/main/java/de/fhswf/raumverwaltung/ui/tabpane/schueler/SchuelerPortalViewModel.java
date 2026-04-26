package de.fhswf.raumverwaltung.ui.tabpane.schueler;

import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.ui.util.VertretungUtil;
import javafx.beans.property.*;
import javafx.collections.*;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Observable;
import java.util.Observer;

public class SchuelerPortalViewModel implements Observer {

    private final SchuelerPortalModel model;

    @Getter
    private final ObservableList<Stunde> tagesStundenProperty
            = FXCollections.observableArrayList();

    @Getter
    private final ObservableList<Stunde> wochenStundenProperty
            = FXCollections.observableArrayList();

    @Getter
    private final StringProperty tagesTitelProperty = new SimpleStringProperty();

    @Getter
    private final StringProperty wochenTitelProperty = new SimpleStringProperty();

    public SchuelerPortalViewModel() {
        this.model = SchuelerPortalModel.getInstance();
        this.model.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        tagesStundenProperty.setAll(model.getTagesStunden());
        wochenStundenProperty.setAll(model.getWochenStunden());
        aktualisiereTitle();
    }

    private void aktualisiereTitle() {
        LocalDate datum    = model.getAktuellesDatum();
        Klasse klasse      = model.getAktuelleKlasse();
        String klassenName = klasse != null
                ? "Klasse " + klasse.getBezeichnung() + " – " : "";

        // Tages-Titel
        String tag = datum.format(
                DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy",
                        java.util.Locale.GERMAN));
        tag = tag.substring(0, 1).toUpperCase() + tag.substring(1);
        tagesTitelProperty.set(klassenName + tag);

        // Wochen-Titel
        LocalDate montag  = datum.with(DayOfWeek.MONDAY);
        LocalDate freitag = datum.with(DayOfWeek.FRIDAY);
        wochenTitelProperty.set(klassenName +
                montag.format(DateTimeFormatter.ofPattern("dd.MM")) +
                " – " +
                freitag.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    }

    public void laden()                { model.laden(); }
    public void navigiereVor()         { model.navigiereTage(1); }
    public void navigiereZurueck()     { model.navigiereTage(-1); }
    public void navigiereWocheVor()    { model.navigiereWochen(1); }
    public void navigiereWocheZurueck() { model.navigiereWochen(-1); }

    public LocalDate getAktuellesDatum() {
        return model.getAktuellesDatum();
    }

    public String getVertretungslehrerName(Stunde stunde, LocalDate datum) {
        return VertretungUtil.getVertretungslehrerName(
                stunde, model.getVertretungenProStunde(), datum
        );
    }

    public boolean hatVertretungAmDatum(Stunde stunde, LocalDate datum) {
        return model.hatVertretungAmDatum(stunde, datum);
    }
}