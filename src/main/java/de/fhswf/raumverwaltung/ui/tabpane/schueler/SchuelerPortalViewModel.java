package de.fhswf.raumverwaltung.ui.tabpane.schueler;

import de.fhswf.raumverwaltung.db.entities.Klasse;
import de.fhswf.raumverwaltung.db.entities.Stunde;
import de.fhswf.raumverwaltung.ui.util.VertretungUtil;
import javafx.beans.property.*;
import javafx.collections.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Observable;
import java.util.Observer;

public class SchuelerPortalViewModel implements Observer {

    private final SchuelerPortalModel model;

    @Getter
    private final ObjectProperty<ObservableList<Stunde>> tagesStundenProperty
            = new SimpleObjectProperty<>(FXCollections.observableArrayList());

    @Getter
    private final ObjectProperty<ObservableList<Stunde>> wochenStundenProperty
            = new SimpleObjectProperty<>(FXCollections.observableArrayList());

    // Anzeige-Titel z.B. "Klasse 7b – Montag, 17.03.2026"
    @Getter
    private final StringProperty titelProperty = new SimpleStringProperty();

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy",
                    java.util.Locale.GERMAN);

    public SchuelerPortalViewModel() {
        this.model = SchuelerPortalModel.getInstance();
        this.model.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        tagesStundenProperty.set(
                FXCollections.observableList(model.getTagesStunden())
        );
        wochenStundenProperty.set(
                FXCollections.observableList(model.getWochenStunden())
        );
        aktualisiereTitle();
    }

    public void laden()             { model.laden(); }
    public void navigiereVor()      { model.navigiereTage(1); }
    public void navigiereZurueck()  { model.navigiereTage(-1); }
    public void navigiereHeute()    { model.navigiereHeute(); }

    public LocalDate getAktuellesDatum() {
        return model.getAktuellesDatum();
    }

    public Klasse getAktuelleKlasse() {
        return model.getAktuelleKlasse();
    }

    private void aktualisiereTitle() {
        Klasse klasse = model.getAktuelleKlasse();
        String klassenName = klasse != null ? "Klasse " + klasse.getBezeichnung() : "";
        String datum = model.getAktuellesDatum().format(FORMATTER);

        // Ersten Buchstaben groß
        datum = datum.substring(0, 1).toUpperCase() + datum.substring(1);

        titelProperty.set(klassenName + " – " + datum);
    }

    public String getVertretungslehrerName(Stunde stunde) {
        return VertretungUtil.getVertretungslehrerName(
                stunde, model.getVertretungenProStunde()
        );
    }
}