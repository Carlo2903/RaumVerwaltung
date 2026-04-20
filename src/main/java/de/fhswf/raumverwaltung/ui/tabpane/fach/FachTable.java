package de.fhswf.raumverwaltung.ui.tabpane.fach;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class FachTable extends TableView<FachTableEntity> {

    public FachTable(FachTableViewModel viewModel) {

        TableColumn<FachTableEntity, String>  colBez = new TableColumn<>("Fach");
        TableColumn<FachTableEntity, String>  colKue = new TableColumn<>("Kürzel");
        TableColumn<FachTableEntity, Integer> colStd = new TableColumn<>("Std/Woche");

        colBez.setCellValueFactory(new PropertyValueFactory<>("bezeichnung"));
        colKue.setCellValueFactory(new PropertyValueFactory<>("kuerzel"));
        colStd.setCellValueFactory(new PropertyValueFactory<>("wochenstundenProKlasse"));

        colBez.setPrefWidth(200);
        colKue.setPrefWidth(100);
        colStd.setPrefWidth(100);

        this.getColumns().addAll(colBez, colKue, colStd);
        this.itemsProperty().bind(viewModel.getFaecherProperty());

        viewModel.refresh();
    }
}