package de.fhswf.raumverwaltung.ui.tabpane.lehrkraft;

import javafx.scene.control.*;

public class LehrkraftTable extends TableView<LehrkraftTableEntity> {

    public LehrkraftTable(LehrkraftTableViewModel viewModel) {

        TableColumn<LehrkraftTableEntity, String>  colName    = new TableColumn<>("Name");
        TableColumn<LehrkraftTableEntity, String>  colKuerzel = new TableColumn<>("Kürzel");
        TableColumn<LehrkraftTableEntity, String>  colFaecher = new TableColumn<>("Fächer");
        TableColumn<LehrkraftTableEntity, Number>  colStd     = new TableColumn<>("Std/W");

        // Lambda statt PropertyValueFactory – typsicher, kein Reflection
        colName.setCellValueFactory(data    -> data.getValue().nameProperty());
        colKuerzel.setCellValueFactory(data -> data.getValue().kuerzelProperty());
        colFaecher.setCellValueFactory(data -> data.getValue().faecherProperty());
        colStd.setCellValueFactory(data     -> data.getValue().sollStundenProperty());

        colName.setPrefWidth(200);
        colKuerzel.setPrefWidth(80);
        colFaecher.setPrefWidth(250);
        colStd.setPrefWidth(80);

        this.getColumns().addAll(colName, colKuerzel, colFaecher, colStd);
        this.itemsProperty().bind(viewModel.getLehrkraefteProperty());

        viewModel.refresh();
    }
}