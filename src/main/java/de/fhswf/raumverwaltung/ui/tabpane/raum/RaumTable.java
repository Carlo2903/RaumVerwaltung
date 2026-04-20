package de.fhswf.raumverwaltung.ui.tabpane.raum;

import de.fhswf.raumverwaltung.db.entities.RaumTyp;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class RaumTable extends TableView<RaumTableEntity> {

    public RaumTable(RaumTableViewModel viewModel) {

        TableColumn<RaumTableEntity, String> colBez = new TableColumn<>("Bezeichnung");
        colBez.setCellValueFactory(data -> data.getValue().bezeichnungProperty());


        TableColumn<RaumTableEntity, RaumTyp> colTyp = new TableColumn<>("Raumtyp");
        colTyp.setCellValueFactory(data -> data.getValue().raumtypProperty());

        TableColumn<RaumTableEntity,Number> colKap = new TableColumn<>("Kapazität");
        colKap.setCellValueFactory(data -> data.getValue().kapazitaetProperty());

        this.getColumns().addAll(colBez, colTyp, colKap);
        this.itemsProperty().bind(viewModel.getRaeumeProperty());

        viewModel.refresh();
    }
}