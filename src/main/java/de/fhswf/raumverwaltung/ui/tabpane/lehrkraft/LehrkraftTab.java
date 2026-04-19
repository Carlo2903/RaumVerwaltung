package de.fhswf.raumverwaltung.ui.tabpane.lehrkraft;

import de.fhswf.raumverwaltung.db.dao.FachDao;
import de.fhswf.raumverwaltung.db.entities.Fach;
import de.fhswf.raumverwaltung.db.entities.Lehrkraft;
import de.fhswf.raumverwaltung.ui.tabpane.MyTab;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LehrkraftTab extends MyTab {

    private final LehrkraftTableViewModel viewModel = new LehrkraftTableViewModel();
    private final LehrkraftTable          table     = new LehrkraftTable(viewModel);
    private final FachDao                 fachDao   = new FachDao();

    // Formularfelder
    private final TextField      nameField    = new TextField();
    private final TextField      kuerzelField = new TextField();
    private final Spinner<Integer> stdSpinner = new Spinner<>(0, 40, 20);

    // Dynamische Fach-Checkboxen: Fach → CheckBox
    private final Map<Fach, CheckBox> fachCheckboxen = new HashMap<>();

    // Aktuell bearbeitete Lehrkraft (null = neue Lehrkraft)
    private Lehrkraft aktuellerDatensatz = null;

    public LehrkraftTab() {
        super("Lehrkräfte");
        this.setContent(buildLayout());
    }

    // ---------------------------------------------------------------
    // Layout
    // ---------------------------------------------------------------

    private BorderPane buildLayout() {
        BorderPane pane = new BorderPane();
        pane.setCenter(table);
        pane.setBottom(buildForm());

        // Zeile anklicken → Formular füllen
        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) fillForm(newVal.getLehrkraft());
                }
        );

        return pane;
    }

    private VBox buildForm() {
        // --- Zeile 1: Name + Kürzel ---
        nameField.setPromptText("Name");
        kuerzelField.setPromptText("Kürzel");
        stdSpinner.setEditable(true);
        stdSpinner.setPrefWidth(80);

        GridPane felder = new GridPane();
        felder.setHgap(16);
        felder.setVgap(8);
        felder.setPadding(new Insets(12));

        felder.add(new Label("Name"),   0, 0);
        felder.add(nameField,           1, 0);
        felder.add(new Label("Kürzel"), 2, 0);
        felder.add(kuerzelField,        3, 0);

        // --- Zeile 2: Fächer als Checkboxen (dynamisch aus DB) ---
        felder.add(new Label("Fächer:"), 0, 1);

        HBox faecherBox = new HBox(12);
        List<Fach> alleFaecher = fachDao.findAll();
        for (Fach fach : alleFaecher) {
            CheckBox cb = new CheckBox(fach.getBezeichnung());
            fachCheckboxen.put(fach, cb);
            faecherBox.getChildren().add(cb);
        }
        felder.add(faecherBox, 1, 1, 2, 1); // colspan 2

        felder.add(new Label("Std/W"), 3, 1);
        felder.add(stdSpinner,         4, 1);

        // --- Zeile 3: Buttons ---
        Button btnAbbrechen  = new Button("Abbrechen");
        Button btnSpeichern  = new Button("Speichern");

        btnAbbrechen.setOnAction(e -> clearForm());
        btnSpeichern.setOnAction(e -> speichern());

        HBox buttons = new HBox(8, btnAbbrechen, btnSpeichern);
        buttons.setPadding(new Insets(0, 12, 12, 0));
        // Speichern-Button rechts ausrichten
        HBox.setHgrow(btnAbbrechen, Priority.ALWAYS);
        buttons.setStyle("-fx-alignment: center-right;");

        VBox formContainer = new VBox(felder, buttons);
        formContainer.setStyle(
                "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 6; " +
                        "-fx-background-radius: 6; " +
                        "-fx-background-color: white;"
        );
        formContainer.setPadding(new Insets(4));

        return formContainer;
    }

    // ---------------------------------------------------------------
    // Formular-Logik
    // ---------------------------------------------------------------

    private void fillForm(Lehrkraft lk) {
        aktuellerDatensatz = lk;
        nameField.setText(lk.getName());
        kuerzelField.setText(lk.getKuerzel());
        stdSpinner.getValueFactory().setValue(lk.getSollStunden());

        // Checkboxen setzen
        fachCheckboxen.forEach((fach, cb) ->
                cb.setSelected(lk.getFaecher().contains(fach))
        );
    }

    private void clearForm() {
        aktuellerDatensatz = null;
        nameField.clear();
        kuerzelField.clear();
        stdSpinner.getValueFactory().setValue(20);
        fachCheckboxen.values().forEach(cb -> cb.setSelected(false));
        table.getSelectionModel().clearSelection();
    }

    private void speichern() {
        // Validierung
        if (nameField.getText().isBlank() || kuerzelField.getText().isBlank()) {
            new Alert(Alert.AlertType.WARNING,
                    "Name und Kürzel dürfen nicht leer sein.")
                    .showAndWait();
            return;
        }

        // Ausgewählte Fächer sammeln
        List<Fach> gewaehlteFaecher = new ArrayList<>();
        fachCheckboxen.forEach((fach, cb) -> {
            if (cb.isSelected()) gewaehlteFaecher.add(fach);
        });

        if (aktuellerDatensatz == null) {
            // Neue Lehrkraft anlegen
            Lehrkraft neu = Lehrkraft.builder()
                    .name(nameField.getText().trim())
                    .kuerzel(kuerzelField.getText().trim())
                    .sollStunden(stdSpinner.getValue())
                    .faecher(gewaehlteFaecher)
                    .sperrzeiten(new ArrayList<>())
                    .build();
            viewModel.speichern(neu);
        } else {
            // Bestehende Lehrkraft aktualisieren
            aktuellerDatensatz.setName(nameField.getText().trim());
            aktuellerDatensatz.setKuerzel(kuerzelField.getText().trim());
            aktuellerDatensatz.setSollStunden(stdSpinner.getValue());
            aktuellerDatensatz.getFaecher().clear();
            aktuellerDatensatz.getFaecher().addAll(gewaehlteFaecher);
            viewModel.speichern(aktuellerDatensatz);
        }

        clearForm();
    }
}