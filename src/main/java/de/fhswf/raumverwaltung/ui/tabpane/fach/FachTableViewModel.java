package de.fhswf.raumverwaltung.ui.tabpane.fach;

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
}