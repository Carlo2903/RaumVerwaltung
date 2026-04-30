package de.fhswf.raumverwaltung.ui.tabpane.lehrkraft;

import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.ui.tabpane.MyTab;
import de.fhswf.raumverwaltung.ui.tabpane.Reloadable;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LehrkraftTab extends MyTab implements Reloadable {

    private final LehrkraftTableViewModel viewModel = new LehrkraftTableViewModel();
    private final LehrkraftTable          table     = new LehrkraftTable(viewModel);

    private final TextField        nameField    = new TextField();
    private final TextField        kuerzelField = new TextField();
    private final Spinner<Integer> stdSpinner   = new Spinner<>(0, 40, 20);

    private final Map<Fach, CheckBox>    fachCheckboxen     = new HashMap<>();
    private final HBox                   faecherBox         = new HBox(12);

    // Sperrzeiten
    private final GridPane               sperrzeitGrid      = new GridPane();
    private final Map<String, CheckBox>  sperrzeitCheckboxen = new HashMap<>();

    private static final Wochentag[] WOCHENTAGE = {
            Wochentag.MONTAG, Wochentag.DIENSTAG, Wochentag.MITTWOCH,
            Wochentag.DONNERSTAG, Wochentag.FREITAG
    };

    public LehrkraftTab() {
        super("Lehrkräfte");
        this.setContent(buildLayout());
        beobachteViewModel();
    }

    private void beobachteViewModel() {
        viewModel.getFaecher().addListener(
                (ListChangeListener<Fach>) c ->
                        baueFachCheckboxen(viewModel.getFaecher())
        );

        // Zeitslots geladen → Grid aufbauen
        viewModel.getAlleZeitslots().addListener(
                (ListChangeListener<Zeitslot>) c -> baueSperrzeitGrid()
        );

        // Sperrzeiten geändert → Grid aktualisieren
        viewModel.getSperrzeiten().addListener(
                (ListChangeListener<Sperrzeit>) c -> aktualisiereSperrzeitGrid()
        );

        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, o, n) -> {
                    if (n != null) {
                        viewModel.datensatzAuswaehlen(n.getLehrkraft());
                        fillFormAusViewModel();
                        viewModel.ladeSperrzeiten(n.getLehrkraft());
                    }
                }
        );

        viewModel.refresh();
    }

    // ---------------------------------------------------------------
    // Layout
    // ---------------------------------------------------------------

    private BorderPane buildLayout() {
        BorderPane pane = new BorderPane();
        pane.setCenter(table);

        ScrollPane formScroll = new ScrollPane(buildUnten());
        formScroll.setFitToWidth(true);
        pane.setBottom(formScroll);

        return pane;
    }

    private VBox buildUnten() {
        return new VBox(buildForm(), buildSperrzeitSektion());
    }

    // ---------------------------------------------------------------
    // Stammdaten-Formular
    // ---------------------------------------------------------------

    private VBox buildForm() {
        nameField.setPromptText("Name");
        kuerzelField.setPromptText("Kürzel");
        stdSpinner.setEditable(true);
        stdSpinner.setPrefWidth(80);

        GridPane felder = new GridPane();
        felder.setHgap(12);
        felder.setVgap(8);
        felder.setPadding(new Insets(12));

        felder.add(new Label("Name:"),    0, 0);
        felder.add(nameField,             1, 0);
        felder.add(new Label("Kürzel:"),  2, 0);
        felder.add(kuerzelField,          3, 0);
        felder.add(new Label("Std/W:"),   4, 0);
        felder.add(stdSpinner,            5, 0);
        felder.add(new Label("Fächer:"),  0, 1);
        felder.add(faecherBox,            1, 1, 5, 1);

        Button btnNeu       = new Button("Neu");
        Button btnSpeichern = new Button("Speichern");
        Button btnLoeschen  = new Button("Löschen");

        btnLoeschen.setStyle(
                "-fx-background-color: #cf222e; -fx-text-fill: white;"
        );

        btnNeu.setOnAction(e -> clearForm());

        btnSpeichern.setOnAction(e -> {
            List<Fach> gewaehlte = new ArrayList<>();
            fachCheckboxen.forEach((fach, cb) -> {
                if (cb.isSelected()) gewaehlte.add(fach);
            });
            viewModel.speichern(
                    nameField.getText().trim(),
                    kuerzelField.getText().trim(),
                    stdSpinner.getValue(),
                    gewaehlte
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
    // Sperrzeiten-Sektion
    // ---------------------------------------------------------------

    private VBox buildSperrzeitSektion() {
        Label titel = new Label("Sperrzeiten");
        titel.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");

        Label hinweis = new Label(
                "✓ = Lehrkraft ist zu diesem Zeitslot nicht verfügbar"
        );
        hinweis.setStyle("-fx-font-size: 11; -fx-text-fill: #555;");

        sperrzeitGrid.setHgap(6);
        sperrzeitGrid.setVgap(6);
        sperrzeitGrid.setPadding(new Insets(8));

        // Grid initial aufbauen falls Zeitslots schon geladen
        if (!viewModel.getAlleZeitslots().isEmpty()) {
            baueSperrzeitGrid();
        }

        Button btnSpeichern = new Button("Sperrzeiten speichern");
        btnSpeichern.setStyle(
                "-fx-background-color: #3d5a80;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 6;"
        );
        btnSpeichern.setOnAction(e -> speichereSperrzeiten());

        VBox sektion = new VBox(8, titel, hinweis,
                sperrzeitGrid, btnSpeichern);
        sektion.setPadding(new Insets(12));
        sektion.setStyle(
                "-fx-border-color: #e0e0e0;" +
                        "-fx-border-width: 1 0 0 0;" +
                        "-fx-background-color: white;"
        );
        return sektion;
    }

    private void baueSperrzeitGrid() {
        sperrzeitGrid.getChildren().clear();
        sperrzeitCheckboxen.clear();

        String[] tage = {"MO", "DI", "MI", "DO", "FR"};

        // Kopfzeile
        for (int i = 0; i < tage.length; i++) {
            Label lbl = new Label(tage[i]);
            lbl.setStyle("-fx-font-weight: bold;");
            lbl.setPrefWidth(50);
            lbl.setAlignment(Pos.CENTER);
            sperrzeitGrid.add(lbl, i + 1, 0);
        }

        // Zeilen: Stunden 1–6
        for (int stunde = 1; stunde <= 6; stunde++) {
            Label stdLabel = new Label(stunde + ".");
            stdLabel.setStyle("-fx-font-weight: bold;");
            sperrzeitGrid.add(stdLabel, 0, stunde);

            for (int tagIdx = 0; tagIdx < WOCHENTAGE.length; tagIdx++) {
                CheckBox cb = new CheckBox();
                cb.setPrefWidth(50);
                cb.setAlignment(Pos.CENTER);

                // Typsicherer Key
                String key = WOCHENTAGE[tagIdx].name() + "_" + stunde;
                sperrzeitCheckboxen.put(key, cb);

                sperrzeitGrid.add(cb, tagIdx + 1, stunde);
            }
        }
    }

    private void aktualisiereSperrzeitGrid() {
        // Alle zurücksetzen
        sperrzeitCheckboxen.values().forEach(cb -> cb.setSelected(false));

        // Gesperrte markieren
        viewModel.getSperrzeiten().forEach(sz -> {
            String key = sz.getZeitslot().getWochentag().name() + "_" +
                    sz.getZeitslot().getStundenNummer();
            CheckBox cb = sperrzeitCheckboxen.get(key);
            if (cb != null) cb.setSelected(true);
        });
    }

    private void speichereSperrzeiten() {
        Lehrkraft lehrkraft = viewModel.getAktuellerDatensatz();
        if (lehrkraft == null) {
            new Alert(Alert.AlertType.WARNING,
                    "Bitte zuerst eine Lehrkraft auswählen.").showAndWait();
            return;
        }

        // Gesperrte Zeitslots aus Map einsammeln
        List<Zeitslot> gesperrt = viewModel.getAlleZeitslots().stream()
                .filter(z -> {
                    String key = z.getWochentag().name() + "_" +
                            z.getStundenNummer();
                    CheckBox cb = sperrzeitCheckboxen.get(key);
                    return cb != null && cb.isSelected();
                })
                .toList();

        viewModel.speichereSperrzeiten(lehrkraft, gesperrt);

        new Alert(Alert.AlertType.INFORMATION,
                "Sperrzeiten erfolgreich gespeichert.").showAndWait();
    }

    // ---------------------------------------------------------------
    // Hilfsmethoden
    // ---------------------------------------------------------------

    private void baueFachCheckboxen(
            javafx.collections.ObservableList<Fach> faecher) {
        faecherBox.getChildren().clear();
        fachCheckboxen.clear();
        for (Fach fach : faecher) {
            CheckBox cb = new CheckBox(fach.getBezeichnung());
            fachCheckboxen.put(fach, cb);
            faecherBox.getChildren().add(cb);
        }
    }

    private void fillFormAusViewModel() {
        Lehrkraft lk = viewModel.getAktuellerDatensatz();
        if (lk == null) return;
        nameField.setText(lk.getName());
        kuerzelField.setText(lk.getKuerzel());
        stdSpinner.getValueFactory().setValue(lk.getSollStunden());
        fachCheckboxen.forEach((fach, cb) ->
                cb.setSelected(lk.getFaecher().contains(fach))
        );
    }

    private void clearForm() {
        viewModel.datensatzAbwaehlen();
        nameField.clear();
        kuerzelField.clear();
        stdSpinner.getValueFactory().setValue(20);
        fachCheckboxen.values().forEach(cb -> cb.setSelected(false));
        sperrzeitCheckboxen.values().forEach(cb -> cb.setSelected(false));
        table.getSelectionModel().clearSelection();
    }

    @Override
    public void reload() {
        viewModel.refresh();
    }
}