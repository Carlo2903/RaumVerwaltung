package de.fhswf.raumverwaltung.ui.tabpane.erziehungsberechtigter;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ErziehungsberechtigterTable
        extends TableView<ErziehungsberechtigterTableEntity> {

    public ErziehungsberechtigterTable(ErziehungsberechtigterTableViewModel viewModel) {

        TableColumn<ErziehungsberechtigterTableEntity, String> colNachname
                = new TableColumn<>("Nachname");
        TableColumn<ErziehungsberechtigterTableEntity, String> colVorname
                = new TableColumn<>("Vorname");
        TableColumn<ErziehungsberechtigterTableEntity, String> colBeziehung
                = new TableColumn<>("Beziehung");
        TableColumn<ErziehungsberechtigterTableEntity, String> colSchueler
                = new TableColumn<>("Schüler");
        TableColumn<ErziehungsberechtigterTableEntity, String> colTelefon
                = new TableColumn<>("Telefon");
        TableColumn<ErziehungsberechtigterTableEntity, String> colEmail
                = new TableColumn<>("E-Mail");

        colNachname.setCellValueFactory(
                data -> data.getValue().nachnameProperty());
        colVorname.setCellValueFactory(
                data -> data.getValue().vornameProperty());
        colBeziehung.setCellValueFactory(
                data -> data.getValue().beziehungProperty());
        colSchueler.setCellValueFactory(
                data -> data.getValue().schuelerProperty());
        colTelefon.setCellValueFactory(
                data -> data.getValue().telefonProperty());
        colEmail.setCellValueFactory(
                data -> data.getValue().emailProperty());

        colNachname.setPrefWidth(140);
        colVorname.setPrefWidth(140);
        colBeziehung.setPrefWidth(100);
        colSchueler.setPrefWidth(160);
        colTelefon.setPrefWidth(120);
        colEmail.setPrefWidth(180);

        this.getColumns().addAll(
                colNachname, colVorname, colBeziehung,
                colSchueler, colTelefon, colEmail
        );

        // Tabelle mit ViewModel verbinden
        viewModel.getErziehungsberechtigteProperty().addListener(
                (obs, o, n) -> { if (n != null) setItems(n); }
        );

        viewModel.refresh();
    }
}
