package de.fhswf.raumverwaltung.ui.tabpane.fach;

import de.fhswf.raumverwaltung.db.entities.Fach;
import javafx.beans.property.*;
import javafx.collections.*;
import lombok.Getter;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

public class FachTableViewModel implements Observer {

    private final FachTableModel model;

    @Getter
    private final ObjectProperty<ObservableList<FachTableEntity>> faecherProperty
            = new SimpleObjectProperty<>();

    @Getter
    private final StringProperty fehlerProperty = new SimpleStringProperty();

    private Fach aktuellerDatensatz = null;

    public FachTableViewModel() {
        this.model = FachTableModel.getInstance();
        this.model.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        List<FachTableEntity> tmp = model.getFaecher().stream()
                .map(FachTableEntity::new)
                .collect(Collectors.toList());
        faecherProperty.set(FXCollections.observableList(tmp));
    }

    public void refresh() {
        model.loadAll();
    }

    public void speichern(String bezeichnung, String kuerzel,
                          int wochenstunden) {
        fehlerProperty.set(null);
        // Validierung
        if (bezeichnung.isBlank()) {
            fehlerProperty.set("Bezeichnung darf nicht leer sein.");
            return;
        }
        if (kuerzel.isBlank()) {
            fehlerProperty.set("Kürzel darf nicht leer sein.");
            return;
        }

        if (aktuellerDatensatz == null) {
            Fach neu = Fach.builder()
                    .bezeichnung(bezeichnung)
                    .kuerzel(kuerzel)
                    .wochenstundenProKlasse(wochenstunden)
                    .build();
            model.speichern(neu);
        } else {
            aktuellerDatensatz.setBezeichnung(bezeichnung);
            aktuellerDatensatz.setKuerzel(kuerzel);
            aktuellerDatensatz.setWochenstundenProKlasse(wochenstunden);
            model.speichern(aktuellerDatensatz);
        }

        fehlerProperty.set(null);
        aktuellerDatensatz = null;
    }

    public void loeschen() {
        fehlerProperty.set(null);

        if (aktuellerDatensatz == null) {
            fehlerProperty.set("Bitte ein Fach auswählen.");
            return;
        }

        if (!model.kannGeloeschtWerden(aktuellerDatensatz)) {
            fehlerProperty.set(
                    "Fach '" + aktuellerDatensatz.getBezeichnung() +
                            "' kann nicht gelöscht werden, " +
                            "da es noch Stunden zugewiesen ist."
            );
            return;
        }

        try {
            model.loeschen(aktuellerDatensatz);
            aktuellerDatensatz = null;
        } catch (jakarta.persistence.PersistenceException e) {
            fehlerProperty.set(
                    "Fach kann nicht gelöscht werden, " +
                            "da es noch Stunden zugewiesen ist."
            );
        }
    }



    public void datensatzAuswaehlen(Fach fach) {
        this.aktuellerDatensatz = fach;
    }

    public void datensatzAbwaehlen() {
        this.aktuellerDatensatz = null;
    }

    public Fach getAktuellerDatensatz() {
        return aktuellerDatensatz;
    }
}