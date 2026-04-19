package de.fhswf.raumverwaltung.ui.tabpane.lehrkraft;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class LehrkraftTable extends TableView<LehrkraftTableEntity> {

    private final LehrkraftTableViewModel viewModel;

    public LehrkraftTable() {
        this.viewModel = new LehrkraftTableViewModel();

        TableColumn<LehrkraftTableEntity, String>  colName  = new TableColumn<>("Name");
        TableColumn<LehrkraftTableEntity, String>  colKuerz = new TableColumn<>("Kürzel");
        TableColumn<LehrkraftTableEntity, Integer> colStd   = new TableColumn<>("Soll-Std/W");

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colKuerz.setCellValueFactory(new PropertyValueFactory<>("kuerzel"));
        colStd.setCellValueFactory(new PropertyValueFactory<>("sollStunden"));

        // Spaltenbreiten
        colName.setPrefWidth(200);
        colKuerz.setPrefWidth(100);
        colStd.setPrefWidth(100);

        this.getColumns().addAll(colName, colKuerz, colStd);
        this.itemsProperty().bind(viewModel.getLehrkraefteProperty());

        viewModel.refresh();
    }
}