package de.fhswf.raumverwaltung.ui.tabpane.klasse;

import de.fhswf.raumverwaltung.db.dao.LehrkraftDao;
import de.fhswf.raumverwaltung.db.entities.Klasse;
import de.fhswf.raumverwaltung.db.entities.Lehrkraft;
import de.fhswf.raumverwaltung.ui.tabpane.lehrkraft.LehrkraftTableEntity;
import javafx.beans.property.*;
import javafx.collections.*;
import lombok.Getter;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

public class KlasseTableViewModel implements Observer {

    private final KlasseTableModel model;

    // DAO nur hier – nie in der View
    private final LehrkraftDao lehrkraftDao = new LehrkraftDao();

    @Getter
    private final ObjectProperty<ObservableList<KlasseTableEntity>> klassenProperty
            = new SimpleObjectProperty<>();

    // Lehrkräfte für ComboBox in der View
    @Getter
    private final ObservableList<Lehrkraft> lehrkraefte
            = FXCollections.observableArrayList();

    @Getter
    private final StringProperty fehlerProperty = new SimpleStringProperty();

    private Klasse aktuellerDatensatz = null;

    public KlasseTableViewModel() {
        this.model = KlasseTableModel.getInstance();
        this.model.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        List<KlasseTableEntity> tmp = model.getKlassen().stream()
                .map(KlasseTableEntity::new)
                .collect(Collectors.toList());
        klassenProperty.set(FXCollections.observableList(tmp));
    }

    public void refresh() {
        // Klassen laden
        model.loadAll();
        // Lehrkräfte für ComboBox laden – kein DAO in der View nötig

        lehrkraefte.setAll(lehrkraftDao.findAll());
    }

    public void speichern(String bezeichnung, int jahrgangsstufe,
                          Lehrkraft klassenLehrer) {
        fehlerProperty.set(null);
        if (bezeichnung.isBlank()) {
            fehlerProperty.set("Bezeichnung darf nicht leer sein.");
            return;
        }

        if (aktuellerDatensatz == null) {
            Klasse neu = Klasse.builder()
                    .bezeichnung(bezeichnung)
                    .jahrgangsstufe(jahrgangsstufe)
                    .klassenLehrer(klassenLehrer)
                    .build();
            model.speichern(neu);
        } else {
            aktuellerDatensatz.setBezeichnung(bezeichnung);
            aktuellerDatensatz.setJahrgangsstufe(jahrgangsstufe);
            aktuellerDatensatz.setKlassenLehrer(klassenLehrer);
            model.speichern(aktuellerDatensatz);
        }

        fehlerProperty.set(null);
        aktuellerDatensatz = null;
    }

    public void loeschen() {
        fehlerProperty.set(null);

        if (aktuellerDatensatz == null) {
            fehlerProperty.set("Bitte eine Klasse auswählen.");
            return;
        }

        if (!model.kannGeloeschtWerden(aktuellerDatensatz)) {
            fehlerProperty.set(
                    "Klasse '" + aktuellerDatensatz.getBezeichnung() +
                            "' kann nicht gelöscht werden, " +
                            "da sie noch Stunden oder Schülern zugewiesen ist."
            );
            return;
        }

        try {
            model.loeschen(aktuellerDatensatz);
            aktuellerDatensatz = null;
        } catch (jakarta.persistence.PersistenceException e) {
            fehlerProperty.set(
                    "Klasse kann nicht gelöscht werden, " +
                            "da sie noch Stunden oder Schülern zugewiesen ist."
            );
        }
    }

    public void datensatzAuswaehlen(Klasse klasse) {
        this.aktuellerDatensatz = klasse;
    }

    public void datensatzAbwaehlen() {
        this.aktuellerDatensatz = null;
    }

    public Klasse getAktuellerDatensatz() {
        return aktuellerDatensatz;
    }
}