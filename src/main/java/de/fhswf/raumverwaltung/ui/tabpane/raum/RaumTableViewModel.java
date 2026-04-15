package de.fhswf.raumverwaltung.ui.tabpane.raum;

import javafx.beans.property.*;
import javafx.collections.*;
import java.util.*;
import java.util.stream.Collectors;

public class RaumTableViewModel implements java.util.Observer {
    private final RaumTableModel model;
    private final ObjectProperty<ObservableList<RaumTableEntity>> raeumeProperty = new SimpleObjectProperty<>();

    public RaumTableViewModel() {
        this.model = RaumTableModel.getInstance();
        this.model.addObserver(this);
    }

    public ObjectProperty<ObservableList<RaumTableEntity>> getRaeumeProperty() { return raeumeProperty; }

    @Override
    public void update(Observable o, Object arg) {
        List<RaumTableEntity> tmp = model.getRaeume().stream()
                .map(RaumTableEntity::new)
                .collect(Collectors.toList());
        raeumeProperty.set(FXCollections.observableList(tmp));
    }

    public void refresh() { model.loadAll(); }
}