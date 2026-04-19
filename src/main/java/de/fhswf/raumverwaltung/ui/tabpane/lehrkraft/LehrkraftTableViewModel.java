package de.fhswf.raumverwaltung.ui.tabpane.lehrkraft;

import de.fhswf.raumverwaltung.db.entities.Lehrkraft;
import javafx.beans.property.*;
import javafx.collections.*;
import lombok.Getter;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

public class LehrkraftTableViewModel implements Observer {

    private final LehrkraftTableModel model;

    @Getter
    private final ObjectProperty<ObservableList<LehrkraftTableEntity>> lehrkraefteProperty
            = new SimpleObjectProperty<>();

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

    public void refresh()                     { model.loadAll(); }
    public void speichern(Lehrkraft lehrkraft) { model.speichern(lehrkraft); }
    public void loeschen(Lehrkraft lehrkraft)  { model.loeschen(lehrkraft); }
}