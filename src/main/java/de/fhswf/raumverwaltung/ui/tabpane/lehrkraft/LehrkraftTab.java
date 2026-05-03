package de.fhswf.raumverwaltung.ui.tabpane.lehrkraft;

import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.ui.tabpane.MyTab;
import de.fhswf.raumverwaltung.ui.tabpane.Reloadable;
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

    private final Map<Fach, CheckBox> fachCheckboxen = new HashMap<>();
    private final HBox                faecherBox     = new HBox(12);

    public LehrkraftTab() {
        super("Lehrkräfte");
        this.setContent(buildLayout());
        beobachteViewModel();
    }

    private void beobachteViewModel() {
        viewModel.getFaecher().addListener(
                (javafx.collections.ListChangeListener<Fach>) c ->
                        baueFachCheckboxen(viewModel.getFaecher())
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

    private BorderPane buildLayout() {
        BorderPane pane = new BorderPane();
        pane.setCenter(table);
        pane.setBottom(buildForm());
        return pane;
    }

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

        Button btnNeu        = new Button("Neu");
        Button btnSpeichern  = new Button("Speichern");
        Button btnLoeschen   = new Button("Löschen");
        Button btnSperrzeiten = new Button("🕐 Sperrzeiten");

        btnLoeschen.setStyle(
                "-fx-background-color: #cf222e; -fx-text-fill: white;"
        );
        btnSperrzeiten.setStyle(
                "-fx-background-color: #3d5a80; -fx-text-fill: white;" +
                        "-fx-background-radius: 6;"
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

        btnSperrzeiten.setOnAction(e -> zeigeSperrzeitDialog());

        HBox buttons = new HBox(8, btnNeu, btnSpeichern,
                btnLoeschen, btnSperrzeiten);
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

    private void zeigeSperrzeitDialog() {
        Lehrkraft lehrkraft = viewModel.getAktuellerDatensatz();
        if (lehrkraft == null) {
            new Alert(Alert.AlertType.WARNING,
                    "Bitte zuerst eine Lehrkraft auswählen.").showAndWait();
            return;
        }

        // Dialog gibt Ergebnis zurück – Tab ruft ViewModel auf
        SperrzeitDialog.zeige(
                lehrkraft,
                viewModel.getAlleZeitslots(),
                viewModel.getSperrzeiten()
        ).ifPresent(gesperrt ->
                viewModel.speichereSperrzeiten(lehrkraft, gesperrt)
        );
    }

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
        table.getSelectionModel().clearSelection();
    }

    @Override
    public void reload() {
        viewModel.refresh();
    }
}