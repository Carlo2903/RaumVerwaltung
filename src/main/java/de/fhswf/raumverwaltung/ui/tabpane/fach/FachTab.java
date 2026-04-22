package de.fhswf.raumverwaltung.ui.tabpane.fach;

import de.fhswf.raumverwaltung.db.entities.Fach;
import de.fhswf.raumverwaltung.ui.tabpane.MyTab;
import de.fhswf.raumverwaltung.ui.tabpane.Reloadable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class FachTab extends MyTab implements Reloadable {

    private final FachTableViewModel viewModel = new FachTableViewModel();
    private final FachTable          table     = new FachTable(viewModel);

    private final TextField        bezeichnungField = new TextField();
    private final TextField        kuerzelField     = new TextField();
    private final Spinner<Integer> stundenSpinner   = new Spinner<>(1, 10, 4);

    public FachTab() {
        super("Fächer");
        this.setContent(buildLayout());
        beobachteViewModel();
    }

    private void beobachteViewModel() {
        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, o, n) -> {
                    if (n != null) {
                        viewModel.datensatzAuswaehlen(n.getFach());
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
        bezeichnungField.setPromptText("z.B. Mathematik");
        kuerzelField.setPromptText("z.B. Ma");
        stundenSpinner.setEditable(true);
        stundenSpinner.setPrefWidth(80);

        GridPane felder = new GridPane();
        felder.setHgap(12);
        felder.setVgap(8);
        felder.setPadding(new Insets(12));

        felder.add(new Label("Bezeichnung:"), 0, 0);
        felder.add(bezeichnungField,           1, 0);
        felder.add(new Label("Kürzel:"),       2, 0);
        felder.add(kuerzelField,               3, 0);
        felder.add(new Label("Std/Woche:"),    4, 0);
        felder.add(stundenSpinner,             5, 0);

        Button btnNeu       = new Button("Neu");
        Button btnSpeichern = new Button("Speichern");
        Button btnLoeschen  = new Button("Löschen");

        btnLoeschen.setStyle(
                "-fx-background-color: #cf222e; -fx-text-fill: white;"
        );

        btnNeu.setOnAction(e -> clearForm());

        btnSpeichern.setOnAction(e -> {
            viewModel.speichern(
                    bezeichnungField.getText().trim(),
                    kuerzelField.getText().trim(),
                    stundenSpinner.getValue()
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
        Fach fach = viewModel.getAktuellerDatensatz();
        if (fach == null) return;
        bezeichnungField.setText(fach.getBezeichnung());
        kuerzelField.setText(fach.getKuerzel());
        stundenSpinner.getValueFactory().setValue(fach.getWochenstundenProKlasse());
    }

    private void clearForm() {
        viewModel.datensatzAbwaehlen();
        bezeichnungField.clear();
        kuerzelField.clear();
        stundenSpinner.getValueFactory().setValue(4);
        table.getSelectionModel().clearSelection();
    }

    @Override
    public void reload() {

    }
}