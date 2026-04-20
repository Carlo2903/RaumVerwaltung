package de.fhswf.raumverwaltung.ui.tabpane.raum;

import de.fhswf.raumverwaltung.db.entities.Raum;
import de.fhswf.raumverwaltung.db.entities.RaumTyp;
import javafx.beans.property.*;
import javafx.collections.*;
import lombok.Getter;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

public class RaumTableViewModel implements Observer {

    private final RaumTableModel model;

    @Getter
    private final ObjectProperty<ObservableList<RaumTableEntity>> raeumeProperty
            = new SimpleObjectProperty<>();

    // Fehlermeldung – View beobachtet diese Property
    @Getter
    private final StringProperty fehlerProperty = new SimpleStringProperty();

    // Aktuell bearbeiteter Datensatz – liegt im ViewModel, nicht in der View
    private Raum aktuellerDatensatz = null;

    public RaumTableViewModel() {
        this.model = RaumTableModel.getInstance();
        this.model.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        List<RaumTableEntity> tmp = model.getRaeume().stream()
                .map(RaumTableEntity::new)
                .collect(Collectors.toList());
        raeumeProperty.set(FXCollections.observableList(tmp));
    }

    public void refresh() {
        model.loadAll();
    }

    // View übergibt nur Rohwerte – ViewModel validiert und baut Entity
    public void speichern(String bezeichnung, RaumTyp raumtyp, int kapazitaet) {
        fehlerProperty.set(null);
        // Validierung
        if (bezeichnung.isBlank()) {
            fehlerProperty.set("Bezeichnung darf nicht leer sein.");
            return;
        }

        if (aktuellerDatensatz == null) {
            Raum neu = Raum.builder()
                    .bezeichnung(bezeichnung)
                    .raumtyp(raumtyp)
                    .kapazitaet(kapazitaet)
                    .build();
            model.speichern(neu);
        } else {
            aktuellerDatensatz.setBezeichnung(bezeichnung);
            aktuellerDatensatz.setRaumtyp(raumtyp);
            aktuellerDatensatz.setKapazitaet(kapazitaet);
            model.speichern(aktuellerDatensatz);
        }

        fehlerProperty.set(null);
        aktuellerDatensatz = null;
    }

    public void loeschen() {
        fehlerProperty.set(null);

        if (aktuellerDatensatz == null) {
            fehlerProperty.set("Bitte einen Raum auswählen.");
            return;
        }

        if (!model.kannGeloeschtWerden(aktuellerDatensatz)) {
            fehlerProperty.set(
                    "Raum '" + aktuellerDatensatz.getBezeichnung() +
                            "' kann nicht gelöscht werden, " +
                            "da er noch Stunden zugewiesen ist."
            );
            return;
        }

        try {
            model.loeschen(aktuellerDatensatz);
            aktuellerDatensatz = null;
        } catch (jakarta.persistence.PersistenceException e) {
            // Fallback falls die Prüfung doch durchgerutscht ist
            fehlerProperty.set(
                    "Raum kann nicht gelöscht werden, " +
                            "da er noch Stunden zugewiesen ist."
            );
        }
    }

    // View meldet Selektion – ViewModel merkt sich den Datensatz
    public void datensatzAuswaehlen(Raum raum) {
        this.aktuellerDatensatz = raum;
    }

    public void datensatzAbwaehlen() {
        this.aktuellerDatensatz = null;
    }

    public Raum getAktuellerDatensatz() {
        return aktuellerDatensatz;
    }
}