package de.fhswf.raumverwaltung.ui.tabpane.schuelerverwaltung;

import de.fhswf.raumverwaltung.db.entities.Klasse;
import de.fhswf.raumverwaltung.db.entities.Schueler;
import de.fhswf.raumverwaltung.db.entities.SchuelerStatus;
import javafx.beans.property.*;
import javafx.collections.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

public class SchuelerVerwaltungTableViewModel implements Observer {

    private final SchuelerVerwaltungTableModel model;

    @Getter
    private final ObjectProperty<ObservableList<SchuelerVerwaltungTableEntity>>
            schuelerProperty = new SimpleObjectProperty<>();

    @Getter
    private final ObservableList<Klasse> klassen
            = FXCollections.observableArrayList();

    @Getter
    private final StringProperty fehlerProperty = new SimpleStringProperty();

    @Getter
    private Schueler aktuellerDatensatz = null;

    public SchuelerVerwaltungTableViewModel() {
        this.model = SchuelerVerwaltungTableModel.getInstance();
        this.model.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        List<SchuelerVerwaltungTableEntity> tmp = model.getSchueler().stream()
                .map(SchuelerVerwaltungTableEntity::new)
                .collect(Collectors.toList());
        schuelerProperty.set(FXCollections.observableList(tmp));
        klassen.setAll(model.getAlleKlassen());
    }

    public void refresh() { model.loadAll(); }

    public void speichern(String vorname, String nachname,
                          LocalDate geburtsdatum, Klasse klasse,
                          SchuelerStatus status) {
        fehlerProperty.set(null);

        if (vorname.isBlank()) {
            fehlerProperty.set("Vorname darf nicht leer sein.");
            return;
        }
        if (nachname.isBlank()) {
            fehlerProperty.set("Nachname darf nicht leer sein.");
            return;
        }
        if (klasse == null) {
            fehlerProperty.set("Bitte eine Klasse auswählen.");
            return;
        }

        if (aktuellerDatensatz == null) {
            Schueler neu = Schueler.builder()
                    .vorname(vorname)
                    .nachname(nachname)
                    .geburtsdatum(geburtsdatum)
                    .klasse(klasse)
                    .status(status != null ? status : SchuelerStatus.AKTIV)
                    .build();
            model.speichern(neu);
        } else {
            aktuellerDatensatz.setVorname(vorname);
            aktuellerDatensatz.setNachname(nachname);
            aktuellerDatensatz.setGeburtsdatum(geburtsdatum);
            aktuellerDatensatz.setKlasse(klasse);
            aktuellerDatensatz.setStatus(
                    status != null ? status : SchuelerStatus.AKTIV
            );
            model.speichern(aktuellerDatensatz);
        }

        fehlerProperty.set(null);
        aktuellerDatensatz = null;
    }

    public void loeschen() {
        fehlerProperty.set(null);

        if (aktuellerDatensatz == null) {
            fehlerProperty.set("Bitte einen Schüler auswählen.");
            return;
        }

        try {
            model.loeschen(aktuellerDatensatz);
            aktuellerDatensatz = null;
        } catch (jakarta.persistence.PersistenceException e) {
            fehlerProperty.set("Schüler kann nicht gelöscht werden.");
        }
    }

    public void datensatzAuswaehlen(Schueler s) { this.aktuellerDatensatz = s; }
    public void datensatzAbwaehlen()            { this.aktuellerDatensatz = null; }
}