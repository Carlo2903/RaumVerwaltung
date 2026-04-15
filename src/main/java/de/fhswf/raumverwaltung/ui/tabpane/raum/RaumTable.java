package de.fhswf.raumverwaltung.ui.tabpane.raum;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class RaumTable extends TableView<RaumTableEntity> {
    private final RaumTableViewModel viewModel;

    public RaumTable() {
        this.viewModel = new RaumTableViewModel();

        TableColumn<RaumTableEntity, String> colBez = new TableColumn<>("Bezeichnung");
        colBez.setCellValueFactory(new PropertyValueFactory<>("bezeichnung"));

        TableColumn<RaumTableEntity, String> colTyp = new TableColumn<>("Raumtyp");
        colTyp.setCellValueFactory(new PropertyValueFactory<>("raumtyp"));

        TableColumn<RaumTableEntity, Integer> colKap = new TableColumn<>("Kapazität");
        colKap.setCellValueFactory(new PropertyValueFactory<>("kapazitaet"));

        this.getColumns().addAll(colBez, colTyp, colKap);
        this.itemsProperty().bind(viewModel.getRaeumeProperty());

        viewModel.refresh();
    }
}