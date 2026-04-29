package de.fhswf.raumverwaltung.ui.tabpane.schuelerverwaltung;

import de.fhswf.raumverwaltung.db.entities.Klasse;
import de.fhswf.raumverwaltung.db.entities.Schueler;
import de.fhswf.raumverwaltung.db.entities.SchuelerStatus;
import de.fhswf.raumverwaltung.ui.tabpane.MyTab;
import de.fhswf.raumverwaltung.ui.tabpane.Reloadable;
import de.fhswf.raumverwaltung.ui.util.EntityStringConverter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;

public class SchuelerVerwaltungTab extends MyTab implements Reloadable {

    private final SchuelerVerwaltungTableViewModel viewModel
            = new SchuelerVerwaltungTableViewModel();
    private final SchuelerVerwaltungTable table
            = new SchuelerVerwaltungTable(viewModel);

    private final TextField                vornameField  = new TextField();
    private final TextField                nachnameField = new TextField();
    private final DatePicker               geburtsDatum  = new DatePicker();
    private final ComboBox<Klasse>         klasseBox     = new ComboBox<>();
    private final ComboBox<SchuelerStatus> statusBox     = new ComboBox<>();

    public SchuelerVerwaltungTab() {
        super("Schüler");
        this.setContent(buildLayout());
        beobachteViewModel();
    }

    private void beobachteViewModel() {
        viewModel.getKlassen().addListener(
                (javafx.collections.ListChangeListener<Klasse>) c ->
                        klasseBox.setItems(viewModel.getKlassen())
        );

        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, o, n) -> {
                    if (n != null) {
                        viewModel.datensatzAuswaehlen(n.getSchueler());
                        fillFormAusViewModel();
                    }
                }
        );
    }

    private BorderPane buildLayout() {
        BorderPane pane = new BorderPane();
        pane.setCenter(table);
        pane.setBottom(buildForm());
        return pane;
    }

    private VBox buildForm() {
        vornameField.setPromptText("Vorname");
        nachnameField.setPromptText("Nachname");
        klasseBox.setPromptText("Klasse wählen...");
        klasseBox.setConverter(EntityStringConverter.forKlasse());
        statusBox.getItems().addAll(SchuelerStatus.values());
        statusBox.setValue(SchuelerStatus.AKTIV);

        GridPane felder = new GridPane();
        felder.setHgap(12);
        felder.setVgap(8);
        felder.setPadding(new Insets(12));

        felder.add(new Label("Vorname:"),      0, 0);
        felder.add(vornameField,               1, 0);
        felder.add(new Label("Nachname:"),     2, 0);
        felder.add(nachnameField,              3, 0);
        felder.add(new Label("Geburtsdatum:"), 4, 0);
        felder.add(geburtsDatum,               5, 0);
        felder.add(new Label("Klasse:"),       0, 1);
        felder.add(klasseBox,                  1, 1);
        felder.add(new Label("Status:"),       2, 1);
        felder.add(statusBox,                  3, 1);

        Button btnNeu       = new Button("Neu");
        Button btnSpeichern = new Button("Speichern");
        Button btnLoeschen  = new Button("Löschen");

        btnLoeschen.setStyle(
                "-fx-background-color: #cf222e; -fx-text-fill: white;"
        );

        btnNeu.setOnAction(e -> clearForm());

        btnSpeichern.setOnAction(e -> {
            viewModel.speichern(
                    vornameField.getText().trim(),
                    nachnameField.getText().trim(),
                    geburtsDatum.getValue(),
                    klasseBox.getValue(),
                    statusBox.getValue()
            );
            String fehler = viewModel.getFehlerProperty().get();
            if (fehler != null) {
                new Alert(Alert.AlertType.WARNING, fehler).showAndWait();
            } else {
                clearForm();
            }
        });

        btnLoeschen.setOnAction(e -> {
            viewModel.loeschen();
            String fehler = viewModel.getFehlerProperty().get();
            if (fehler != null) {
                new Alert(Alert.AlertType.WARNING, fehler).showAndWait();
            } else {
                clearForm();
            }
        });

        HBox buttons = new HBox(8, btnNeu, btnSpeichern, btnLoeschen);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(0, 12, 12, 12));

        VBox form = new VBox(felder, buttons);
        form.setStyle(
                "-fx-border-color: #e0e0e0;" +
                        "-fx-border-width: 1 0 0 0;" +
                        "-fx-background-color: white;"
        );
        return form;
    }

    private void fillFormAusViewModel() {
        Schueler s = viewModel.getAktuellerDatensatz();
        if (s == null) return;
        vornameField.setText(s.getVorname());
        nachnameField.setText(s.getNachname());
        geburtsDatum.setValue(s.getGeburtsdatum());
        klasseBox.setValue(s.getKlasse());
        statusBox.setValue(s.getStatus());
    }

    private void clearForm() {
        viewModel.datensatzAbwaehlen();
        vornameField.clear();
        nachnameField.clear();
        geburtsDatum.setValue(null);
        klasseBox.setValue(null);
        statusBox.setValue(SchuelerStatus.AKTIV);
        table.getSelectionModel().clearSelection();
    }

    @Override
    public void reload() { viewModel.refresh(); }
}