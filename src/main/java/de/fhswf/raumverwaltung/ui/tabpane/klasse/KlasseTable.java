package de.fhswf.raumverwaltung.ui.tabpane.klasse;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class KlasseTable extends TableView<KlasseTableEntity> {

    private final KlasseTableViewModel viewModel;

    public KlasseTable() {
        this.viewModel = new KlasseTableViewModel();

        TableColumn<KlasseTableEntity, String>  colBez   = new TableColumn<>("Klasse");
        TableColumn<KlasseTableEntity, Integer> colJahr  = new TableColumn<>("Jahrgang");
        TableColumn<KlasseTableEntity, String>  colLehrer = new TableColumn<>("Klassenlehrer");

        colBez.setCellValueFactory(new PropertyValueFactory<>("bezeichnung"));
        colJahr.setCellValueFactory(new PropertyValueFactory<>("jahrgangsstufe"));
        colLehrer.setCellValueFactory(new PropertyValueFactory<>("klassenlehrer"));

        colBez.setPrefWidth(100);
        colJahr.setPrefWidth(100);
        colLehrer.setPrefWidth(200);

        this.getColumns().addAll(colBez, colJahr, colLehrer);
        this.itemsProperty().bind(viewModel.getKlassenProperty());

        viewModel.refresh();
    }
}