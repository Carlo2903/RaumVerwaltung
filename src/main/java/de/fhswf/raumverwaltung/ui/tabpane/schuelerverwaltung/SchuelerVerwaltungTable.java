package de.fhswf.raumverwaltung.ui.tabpane.schuelerverwaltung;

import javafx.scene.control.*;

public class SchuelerVerwaltungTable
        extends TableView<SchuelerVerwaltungTableEntity> {

    public SchuelerVerwaltungTable(SchuelerVerwaltungTableViewModel viewModel) {

        TableColumn<SchuelerVerwaltungTableEntity, String> colNachname
                = new TableColumn<>("Nachname");
        TableColumn<SchuelerVerwaltungTableEntity, String> colVorname
                = new TableColumn<>("Vorname");
        TableColumn<SchuelerVerwaltungTableEntity, String> colGeburtsdatum
                = new TableColumn<>("Geburtsdatum");
        TableColumn<SchuelerVerwaltungTableEntity, String> colKlasse
                = new TableColumn<>("Klasse");
        TableColumn<SchuelerVerwaltungTableEntity, String> colStatus
                = new TableColumn<>("Status");

        colNachname.setCellValueFactory(
                data -> data.getValue().nachnameProperty());
        colVorname.setCellValueFactory(
                data -> data.getValue().vornameProperty());
        colGeburtsdatum.setCellValueFactory(
                data -> data.getValue().geburtsdatumProperty());
        colKlasse.setCellValueFactory(
                data -> data.getValue().klasseProperty());
        colStatus.setCellValueFactory(
                data -> data.getValue().statusProperty());

        colNachname.setPrefWidth(150);
        colVorname.setPrefWidth(150);
        colGeburtsdatum.setPrefWidth(120);
        colKlasse.setPrefWidth(80);
        colStatus.setPrefWidth(100);

        this.getColumns().addAll(
                colNachname, colVorname, colGeburtsdatum, colKlasse, colStatus
        );

        viewModel.getSchuelerProperty().addListener(
                (obs, o, n) -> { if (n != null) setItems(n); }
        );

        viewModel.refresh();
    }
}