package de.fhswf.raumverwaltung.ui.tabpane.raum;

import de.fhswf.raumverwaltung.db.entities.Raum;
import de.fhswf.raumverwaltung.db.entities.RaumTyp;
import de.fhswf.raumverwaltung.ui.tabpane.MyTab;
import de.fhswf.raumverwaltung.ui.tabpane.Reloadable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class RaumTab extends MyTab implements Reloadable {

    private final RaumTableViewModel viewModel = new RaumTableViewModel();
    private final RaumTable          table     = new RaumTable(viewModel);

    private final TextField          bezeichnungField  = new TextField();
    private final ComboBox<RaumTyp>  raumtypBox        = new ComboBox<>();
    private final Spinner<Integer>   kapazitaetSpinner = new Spinner<>(0, 500, 30);

    public RaumTab() {
        super("Räume");
        this.setContent(buildLayout());
        beobachteViewModel();
    }

    private void beobachteViewModel() {


        // Selektion → Formular füllen (über ViewModel)
        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, o, n) -> {
                    if (n != null) {
                        viewModel.datensatzAuswaehlen(n.getRaum());
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
        bezeichnungField.setPromptText("z.B. R101");
        raumtypBox.getItems().addAll(RaumTyp.values());
        raumtypBox.setValue(RaumTyp.STANDARD);
        kapazitaetSpinner.setEditable(true);
        kapazitaetSpinner.setPrefWidth(90);

        GridPane felder = new GridPane();
        felder.setHgap(12);
        felder.setVgap(8);
        felder.setPadding(new Insets(12));

        felder.add(new Label("Bezeichnung:"), 0, 0);
        felder.add(bezeichnungField,           1, 0);
        felder.add(new Label("Raumtyp:"),      2, 0);
        felder.add(raumtypBox,                 3, 0);
        felder.add(new Label("Kapazität:"),    4, 0);
        felder.add(kapazitaetSpinner,          5, 0);

        Button btnNeu       = new Button("Neu");
        Button btnSpeichern = new Button("Speichern");
        Button btnLoeschen  = new Button("Löschen");

        btnLoeschen.setStyle(
                "-fx-background-color: #cf222e; -fx-text-fill: white;"
        );

        // View übergibt nur Rohwerte – keine Logik
        btnNeu.setOnAction(e -> clearForm());

        btnSpeichern.setOnAction(e -> {
            viewModel.speichern(
                    bezeichnungField.getText().trim(),
                    raumtypBox.getValue(),
                    kapazitaetSpinner.getValue()
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
                // Fehler vorhanden → anzeigen, Formular bleibt offen
                new Alert(Alert.AlertType.WARNING, fehler).showAndWait();
            } else {
                // Kein Fehler → Formular leeren
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

    // Formular füllen – Werte kommen aus ViewModel
    private void fillFormAusViewModel() {
        Raum raum = viewModel.getAktuellerDatensatz();
        if (raum == null) return;
        bezeichnungField.setText(raum.getBezeichnung());
        raumtypBox.setValue(raum.getRaumtyp());
        kapazitaetSpinner.getValueFactory().setValue(raum.getKapazitaet());
    }

    private void clearForm() {
        viewModel.datensatzAbwaehlen();
        bezeichnungField.clear();
        raumtypBox.setValue(RaumTyp.STANDARD);
        kapazitaetSpinner.getValueFactory().setValue(30);
        table.getSelectionModel().clearSelection();
    }

    @Override
    public void reload() {

    }
}