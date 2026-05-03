package de.fhswf.raumverwaltung.ui.tabpane.erziehungsberechtigter;

import de.fhswf.raumverwaltung.db.entities.Beziehung;
import de.fhswf.raumverwaltung.db.entities.Erziehungsberechtigter;
import de.fhswf.raumverwaltung.db.entities.Schueler;
import de.fhswf.raumverwaltung.ui.tabpane.MyTab;
import de.fhswf.raumverwaltung.ui.tabpane.Reloadable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ErziehungsberechtigterTab extends MyTab implements Reloadable {

    private final ErziehungsberechtigterTableViewModel viewModel
            = new ErziehungsberechtigterTableViewModel();
    private final ErziehungsberechtigterTable table
            = new ErziehungsberechtigterTable(viewModel);

    // Formular-Felder
    private final TextField              vornameField  = new TextField();
    private final TextField              nachnameField = new TextField();
    private final ComboBox<Beziehung>    beziehungBox  = new ComboBox<>();
    private final ComboBox<Schueler>     schuelerBox   = new ComboBox<>();
    private final TextField              adresseField  = new TextField();
    private final TextField              telefonField  = new TextField();
    private final TextField              emailField    = new TextField();

    public ErziehungsberechtigterTab() {
        super("Erziehungsber.");
        this.setContent(buildLayout());
        beobachteViewModel();
    }

    // ---------------------------------------------------------------
    // Observer-Verbindungen
    // ---------------------------------------------------------------

    private void beobachteViewModel() {
        // Schüler-Liste → ComboBox befüllen
        viewModel.getSchueler().addListener(
                (javafx.collections.ListChangeListener<Schueler>) c ->
                        schuelerBox.setItems(viewModel.getSchueler())
        );

        // Tabellenzeile angeklickt → Formular befüllen
        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, o, n) -> {
                    if (n != null) {
                        viewModel.datensatzAuswaehlen(n.getErziehungsberechtigter());
                        fillFormAusViewModel();
                    }
                }
        );
    }

    // ---------------------------------------------------------------
    // Layout
    // ---------------------------------------------------------------

    private BorderPane buildLayout() {
        BorderPane pane = new BorderPane();
        pane.setCenter(table);
        pane.setBottom(buildForm());
        return pane;
    }

    // ---------------------------------------------------------------
    // Formular
    // ---------------------------------------------------------------

    private VBox buildForm() {
        vornameField.setPromptText("Vorname");
        nachnameField.setPromptText("Nachname");
        adresseField.setPromptText("Straße, PLZ Ort");
        telefonField.setPromptText("Telefonnummer");
        emailField.setPromptText("E-Mail-Adresse");

        beziehungBox.getItems().addAll(Beziehung.values());
        beziehungBox.setPromptText("Beziehung wählen...");

        schuelerBox.setPromptText("Schüler wählen...");
        // Schüler lesbar darstellen: "Nachname, Vorname"
        schuelerBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Schueler s) {
                return s == null ? "" : s.getNachname() + ", " + s.getVorname();
            }
            @Override
            public Schueler fromString(String string) { return null; }
        });

        GridPane felder = new GridPane();
        felder.setHgap(12);
        felder.setVgap(8);
        felder.setPadding(new Insets(12));

        // Zeile 0: Vorname – Nachname – Beziehung
        felder.add(new Label("Vorname:"),   0, 0); felder.add(vornameField,  1, 0);
        felder.add(new Label("Nachname:"),  2, 0); felder.add(nachnameField, 3, 0);
        felder.add(new Label("Beziehung:"), 4, 0); felder.add(beziehungBox,  5, 0);

        // Zeile 1: Schüler – Adresse
        felder.add(new Label("Schüler:"),   0, 1); felder.add(schuelerBox,  1, 1);
        felder.add(new Label("Adresse:"),   2, 1); felder.add(adresseField, 3, 1, 3, 1);

        // Zeile 2: Telefon – E-Mail
        felder.add(new Label("Telefon:"),   0, 2); felder.add(telefonField, 1, 2);
        felder.add(new Label("E-Mail:"),    2, 2); felder.add(emailField,   3, 2, 3, 1);

        // Felder wachsen mit
        GridPane.setHgrow(vornameField,  Priority.ALWAYS);
        GridPane.setHgrow(nachnameField, Priority.ALWAYS);
        GridPane.setHgrow(beziehungBox,  Priority.ALWAYS);
        GridPane.setHgrow(schuelerBox,   Priority.ALWAYS);
        GridPane.setHgrow(adresseField,  Priority.ALWAYS);
        GridPane.setHgrow(telefonField,  Priority.ALWAYS);
        GridPane.setHgrow(emailField,    Priority.ALWAYS);

        // Buttons
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
                    beziehungBox.getValue(),
                    schuelerBox.getValue(),
                    adresseField.getText().trim(),
                    telefonField.getText().trim(),
                    emailField.getText().trim()
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

    // ---------------------------------------------------------------
    // Hilfsmethoden
    // ---------------------------------------------------------------

    private void fillFormAusViewModel() {
        Erziehungsberechtigter e = viewModel.getAktuellerDatensatz();
        if (e == null) return;
        vornameField.setText(e.getVorname());
        nachnameField.setText(e.getNachname());
        beziehungBox.setValue(e.getBeziehung());
        schuelerBox.setValue(e.getSchueler());
        adresseField.setText(e.getAdresse() != null ? e.getAdresse() : "");
        telefonField.setText(e.getTelefon() != null ? e.getTelefon() : "");
        emailField.setText(e.getEmail() != null ? e.getEmail() : "");
    }

    private void clearForm() {
        viewModel.datensatzAbwaehlen();
        vornameField.clear();
        nachnameField.clear();
        beziehungBox.setValue(null);
        schuelerBox.setValue(null);
        adresseField.clear();
        telefonField.clear();
        emailField.clear();
        table.getSelectionModel().clearSelection();
    }

    @Override
    public void reload() { viewModel.refresh(); }
}
