package de.fhswf.raumverwaltung.ui.tabpane.lehrkraft;

import de.fhswf.raumverwaltung.db.dao.FachDao;
import de.fhswf.raumverwaltung.db.entities.Fach;
import de.fhswf.raumverwaltung.db.entities.Lehrkraft;
import de.fhswf.raumverwaltung.db.entities.Sperrzeit;
import de.fhswf.raumverwaltung.db.entities.Zeitslot;
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
    private final FachDao             fachDao = new FachDao();

    @Getter
    private final ObjectProperty<ObservableList<LehrkraftTableEntity>> lehrkraefteProperty
            = new SimpleObjectProperty<>();

    @Getter
    private final ObservableList<Fach> faecher
            = FXCollections.observableArrayList();

    @Getter
    private final StringProperty fehlerProperty = new SimpleStringProperty();

    @Getter
    private final ObservableList<Sperrzeit> sperrzeiten
            = FXCollections.observableArrayList();

    @Getter
    private final ObservableList<Zeitslot> alleZeitslots
            = FXCollections.observableArrayList();

    @Getter
    private Lehrkraft aktuellerDatensatz = null;

    public LehrkraftTableViewModel() {
        this.model = LehrkraftTableModel.getInstance();
        this.model.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if ("SPERRZEITEN".equals(arg)) {
            // Nur Sperrzeiten aktualisieren – Table bleibt unberührt
            javafx.application.Platform.runLater(() ->
                    sperrzeiten.setAll(model.getAktuelleSperrzeiten())
            );
            return;
        }

        // Voller Reload
        fachDao.clearCache();
        List<LehrkraftTableEntity> tmp = model.getLehrkraefte().stream()
                .map(LehrkraftTableEntity::new)
                .collect(Collectors.toList());

        javafx.application.Platform.runLater(() -> {
            lehrkraefteProperty.set(FXCollections.observableList(tmp));
            faecher.setAll(fachDao.findAll());
            alleZeitslots.setAll(model.getAlleZeitslots());
        });
    }

    public void ladeSperrzeiten(Lehrkraft lehrkraft) {
        model.ladeSperrzeiten(lehrkraft);
    }

    public void speichereSperrzeiten(Lehrkraft lehrkraft,
                                     List<Zeitslot> gesperrteZeitslots) {
        model.speichereSperrzeiten(lehrkraft, gesperrteZeitslots);
    }

    public void refresh() {
        model.loadAll();
    }

    public void speichern(String name, String kuerzel,
                          int sollStunden, List<Fach> gewaehlteFaecher) {
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
                    .faecher(new ArrayList<>(gewaehlteFaecher))
                    .sperrzeiten(new ArrayList<>())
                    .build();
            model.speichern(neu);
        } else {
            aktuellerDatensatz.setName(name);
            aktuellerDatensatz.setKuerzel(kuerzel);
            aktuellerDatensatz.setSollStunden(sollStunden);
            aktuellerDatensatz.getFaecher().clear();
            aktuellerDatensatz.getFaecher().addAll(gewaehlteFaecher);
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
                            "da sie noch Stunden, Abwesenheiten oder Vertretungen zugewiesen ist."
            );
            return;
        }

        try {
            model.loeschen(aktuellerDatensatz);
            aktuellerDatensatz = null;
        } catch (jakarta.persistence.PersistenceException e) {
            fehlerProperty.set(
                    "Lehrkraft kann nicht gelöscht werden, " +
                            "da sie noch in anderen Daten (Stunden, Abwesenheiten, Vertretungen) verwendet wird."
            );
        }
    }

    public void datensatzAuswaehlen(Lehrkraft lehrkraft) {
        this.aktuellerDatensatz = lehrkraft;
    }

    public void datensatzAbwaehlen() {
        this.aktuellerDatensatz = null;
    }

}