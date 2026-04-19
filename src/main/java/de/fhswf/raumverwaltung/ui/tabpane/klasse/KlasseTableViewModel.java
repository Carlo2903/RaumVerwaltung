package de.fhswf.raumverwaltung.ui.tabpane.klasse;

import javafx.beans.property.*;
import javafx.collections.*;
import lombok.Getter;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

public class KlasseTableViewModel implements Observer {

    private final KlasseTableModel model;

    @Getter
    private final ObjectProperty<ObservableList<KlasseTableEntity>> klassenProperty
            = new SimpleObjectProperty<>();

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
        model.loadAll();
    }
}