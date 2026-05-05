package de.fhswf.raumverwaltung.ui.tabpane.klasse;

import de.fhswf.raumverwaltung.db.entities.Klasse;
import de.fhswf.raumverwaltung.db.entities.Lehrkraft;
import de.fhswf.raumverwaltung.ui.tabpane.MyTab;
import de.fhswf.raumverwaltung.ui.tabpane.Reloadable;
import de.fhswf.raumverwaltung.ui.util.EntityStringConverter;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class KlasseTab extends MyTab implements Reloadable {

    private final KlasseTableViewModel  viewModel = new KlasseTableViewModel();
    private final KlasseTable           table     = new KlasseTable(viewModel);

    private final TextField           bezeichnungField = new TextField();
    private final Spinner<Integer>    jahrgangSpinner  = new Spinner<>(5, 10, 5);
    private final ComboBox<Lehrkraft> klassenlehrerBox = new ComboBox<>();


    public KlasseTab() {
        super("Klassen");
        this.setContent(buildLayout());
        beobachteViewModel();
    }




    private void beobachteViewModel() {
        // Lehrkräfte für ComboBox – kommen aus ViewModel
        viewModel.getLehrkraefte().addListener(
                (ListChangeListener<Lehrkraft>) change ->
                        klassenlehrerBox.setItems(viewModel.getLehrkraefte())

        );



        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, o, n) -> {
                    if (n != null) {
                        viewModel.datensatzAuswaehlen(n.getKlasse());
                        fillFormAusViewModel();
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
        bezeichnungField.setPromptText("z.B. 7b");
        jahrgangSpinner.setEditable(true);
        jahrgangSpinner.setPrefWidth(80);
        klassenlehrerBox.setPromptText("Klassenlehrer wählen...");
        klassenlehrerBox.setConverter(EntityStringConverter.forLehrkraft());

        GridPane felder = new GridPane();
        felder.setHgap(12);
        felder.setVgap(8);
        felder.setPadding(new Insets(12));

        felder.add(new Label("Bezeichnung:"),  0, 0);
        felder.add(bezeichnungField,            1, 0);
        felder.add(new Label("Jahrgang:"),      2, 0);
        felder.add(jahrgangSpinner,             3, 0);
        felder.add(new Label("Klassenlehrer:"), 4, 0);
        felder.add(klassenlehrerBox,            5, 0);

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
                    jahrgangSpinner.getValue(),
                    klassenlehrerBox.getValue()
            );
            String fehler = viewModel.getFehlerProperty().get();
            if (fehler != null) {
                new Alert(Alert.AlertType.WARNING, fehler).showAndWait();
            } else {
                clearForm();
            }
        });

        btnLoeschen.setOnAction(e -> {
            if (viewModel.getAktuellerDatensatz() == null) {
                new Alert(Alert.AlertType.WARNING, "Bitte wählen Sie einen Datensatz zum Löschen aus.").showAndWait();
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Möchten Sie diesen Datensatz wirklich löschen?");
            confirm.setTitle("Löschen bestätigen");
            confirm.setHeaderText(null);

            confirm.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.OK) {
                    viewModel.loeschen();
                    String fehler = viewModel.getFehlerProperty().get();
                    if (fehler != null) {
                        new Alert(Alert.AlertType.WARNING, fehler).showAndWait();
                    } else {
                        clearForm();
                    }
                }
            });
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
        Klasse klasse = viewModel.getAktuellerDatensatz();
        if (klasse == null) return;
        bezeichnungField.setText(klasse.getBezeichnung());
        jahrgangSpinner.getValueFactory().setValue(klasse.getJahrgangsstufe());
        klassenlehrerBox.setValue(klasse.getKlassenLehrer());
    }

    private void clearForm() {
        viewModel.datensatzAbwaehlen();
        bezeichnungField.clear();
        jahrgangSpinner.getValueFactory().setValue(5);
        klassenlehrerBox.setValue(null);
        table.getSelectionModel().clearSelection();
    }

    @Override
    public void reload() {
        viewModel.refresh();
    }
}