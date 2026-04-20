package de.fhswf.raumverwaltung.ui.tabpane.lehrkraft;

import de.fhswf.raumverwaltung.db.dao.FachDao;
import de.fhswf.raumverwaltung.db.entities.Fach;
import de.fhswf.raumverwaltung.db.entities.Lehrkraft;
import javafx.beans.property.*;
import javafx.collections.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

public class LehrkraftTableViewModel implements Observer {

    private final LehrkraftTableModel model;

    // DAO nur hier – nie in der View
    private final FachDao fachDao = new FachDao();

    @Getter
    private final ObjectProperty<ObservableList<LehrkraftTableEntity>> lehrkraefteProperty
            = new SimpleObjectProperty<>();

    // Alle Fächer für Checkboxen in der View
    @Getter
    private final ObjectProperty<ObservableList<Fach>> faecherProperty
            = new SimpleObjectProperty<>();

    @Getter
    private final StringProperty fehlerProperty = new SimpleStringProperty();

    private Lehrkraft aktuellerDatensatz = null;

    public LehrkraftTableViewModel() {
        this.model = LehrkraftTableModel.getInstance();
        this.model.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        List<LehrkraftTableEntity> tmp = model.getLehrkraefte().stream()
                .map(LehrkraftTableEntity::new)
                .collect(Collectors.toList());
        lehrkraefteProperty.set(FXCollections.observableList(tmp));
    }

    public void refresh() {
        model.loadAll();
        // Fächer für Checkboxen laden – kein DAO in der View nötig
        faecherProperty.set(
                FXCollections.observableList(fachDao.findAll())
        );
    }

    public void speichern(String name, String kuerzel,
                          int sollStunden, List<Fach> faecher) {
        fehlerProperty.set(null);
        if (name.isBlank()) {
            fehlerProperty.set("Name darf nicht leer sein.");
            return;
        }
        if (kuerzel.isBlank()) {
            fehlerProperty.set("Kürzel darf nicht leer sein.");
            return;
        }

        if (aktuellerDatensatz == null) {
            Lehrkraft neu = Lehrkraft.builder()
                    .name(name)
                    .kuerzel(kuerzel)
                    .sollStunden(sollStunden)
                    .faecher(faecher)
                    .sperrzeiten(new ArrayList<>())
                    .build();
            model.speichern(neu);
        } else {
            aktuellerDatensatz.setName(name);
            aktuellerDatensatz.setKuerzel(kuerzel);
            aktuellerDatensatz.setSollStunden(sollStunden);
            aktuellerDatensatz.getFaecher().clear();
            aktuellerDatensatz.getFaecher().addAll(faecher);
            model.speichern(aktuellerDatensatz);
        }

        fehlerProperty.set(null);
        aktuellerDatensatz = null;
    }

    public void loeschen() {
        fehlerProperty.set(null);

        if (aktuellerDatensatz == null) {
            fehlerProperty.set("Bitte eine Lehrkraft auswählen.");
            return;
        }

        if (!model.kannGeloeschtWerden(aktuellerDatensatz)) {
            fehlerProperty.set(
                    "Lehrkraft '" + aktuellerDatensatz.getName() +
                            "' kann nicht gelöscht werden, " +
                            "da sie noch Stunden zugewiesen ist."
            );
            return;
        }

        try {
            model.loeschen(aktuellerDatensatz);
            aktuellerDatensatz = null;
        } catch (jakarta.persistence.PersistenceException e) {
            fehlerProperty.set(
                    "Lehrkraft kann nicht gelöscht werden, " +
                            "da sie noch Stunden zugewiesen ist."
            );
        }
    }

    public void datensatzAuswaehlen(Lehrkraft lehrkraft) {
        this.aktuellerDatensatz = lehrkraft;
    }

    public void datensatzAbwaehlen() {
        this.aktuellerDatensatz = null;
    }

    public Lehrkraft getAktuellerDatensatz() {
        return aktuellerDatensatz;
    }
}