package de.fhswf.raumverwaltung.ui.tabpane.stundenplan;

import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.ui.util.EntityStringConverter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.text.*;

import java.util.Map;

public class StundenplanRasterView extends BorderPane {

    private final StundenplanViewModel viewModel;

    // Wochentage als Spalten
    private static final Wochentag[] TAGE = {
            Wochentag.MONTAG,
            Wochentag.DIENSTAG,
            Wochentag.MITTWOCH,
            Wochentag.DONNERSTAG,
            Wochentag.FREITAG
    };

    // Anzahl Stunden pro Tag
    private static final int STUNDEN_PRO_TAG = 6;

    private final GridPane grid = new GridPane();

    public StundenplanRasterView(StundenplanViewModel viewModel) {
        this.viewModel = viewModel;

        this.setTop(buildHeader());
        this.setCenter(buildGrid());

        // Auf Model-Updates reagieren
        viewModel.getGridProperty().addListener(
                (obs, oldVal, newVal) -> aktualisiereGrid(newVal)
        );

        viewModel.laden();
    }

    // ---------------------------------------------------------------
    // Header: Titel + Klassen-Filter
    // ---------------------------------------------------------------

    private HBox buildHeader() {
        Label titel = new Label("Stundenplan");
        titel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Label lblKlasse = new Label("Klasse:");

        ComboBox<Klasse> klasseBox = new ComboBox<>();
        klasseBox.setPromptText("Alle Klassen");
        klasseBox.setConverter(EntityStringConverter.forKlasse());

        // Klassen in ComboBox anzeigen
        viewModel.getKlassenProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) klasseBox.setItems(newVal);
                }
        );

        // Klasse wählen → Filter anwenden
        klasseBox.setOnAction(e ->
                viewModel.filterNachKlasse(klasseBox.getValue())
        );

        // "Alle" Button
        Button btnAlle = new Button("Alle");
        btnAlle.setOnAction(e -> {
            klasseBox.setValue(null);
            viewModel.filterNachKlasse(null);
        });

        HBox header = new HBox(16, titel, new Separator(),
                lblKlasse, klasseBox, btnAlle);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(12, 16, 12, 16));
        header.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 0 0 1 0;");
        return header;
    }

    // ---------------------------------------------------------------
    // Grid aufbauen
    // ---------------------------------------------------------------

    private ScrollPane buildGrid() {
        grid.setHgap(4);
        grid.setVgap(4);
        grid.setPadding(new Insets(12));
        grid.setStyle("-fx-background-color: #f5f5f5;");

        // Spaltenbreiten gleichmäßig verteilen
        // Spalte 0 = Zeitslot-Label (schmal)
        ColumnConstraints colZeit = new ColumnConstraints();
        colZeit.setPrefWidth(60);
        grid.getColumnConstraints().add(colZeit);

        for (int i = 0; i < TAGE.length; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setHgrow(Priority.ALWAYS);
            col.setMinWidth(120);
            grid.getColumnConstraints().add(col);
        }

        // Kopfzeile: Wochentage
        grid.add(new Label(""), 0, 0); // leere Ecke
        for (int i = 0; i < TAGE.length; i++) {
            Label tagLabel = new Label(TAGE[i].name().substring(0, 2));
            tagLabel.setStyle(
                    "-fx-font-weight: bold; " +
                            "-fx-font-size: 13; " +
                            "-fx-alignment: center;"
            );
            tagLabel.setMaxWidth(Double.MAX_VALUE);
            tagLabel.setAlignment(Pos.CENTER);
            tagLabel.setPadding(new Insets(6));
            tagLabel.setStyle(tagLabel.getStyle() +
                    "-fx-background-color: #3d5a80; -fx-text-fill: white; " +
                    "-fx-background-radius: 6;");
            grid.add(tagLabel, i + 1, 0);
        }

        // Zeilen: Stundennummern
        for (int stunde = 1; stunde <= STUNDEN_PRO_TAG; stunde++) {
            Label stdLabel = new Label(stunde + ".");
            stdLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
            stdLabel.setPadding(new Insets(4));
            grid.add(stdLabel, 0, stunde);

            // Leere Zellen als Platzhalter
            for (int tag = 0; tag < TAGE.length; tag++) {
                grid.add(leereZelle(TAGE[tag], stunde), tag + 1, stunde);
            }
        }

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        return scroll;
    }

    // ---------------------------------------------------------------
    // Grid aktualisieren wenn Model sich ändert
    // ---------------------------------------------------------------

    private void aktualisiereGrid(Map<Wochentag, Map<Integer, Stunde>> stundenGrid) {
        if (stundenGrid == null) return;

        for (int stunde = 1; stunde <= STUNDEN_PRO_TAG; stunde++) {
            for (int tagIdx = 0; tagIdx < TAGE.length; tagIdx++) {
                Wochentag tag = TAGE[tagIdx];

                // Alte Zelle entfernen
                final int col = tagIdx + 1;
                final int row = stunde;
                grid.getChildren().removeIf(node ->
                        GridPane.getColumnIndex(node) != null &&
                                GridPane.getColumnIndex(node) == col &&
                                GridPane.getRowIndex(node) != null &&
                                GridPane.getRowIndex(node) == row
                );

                // Neue Zelle setzen
                Stunde s = stundenGrid
                        .getOrDefault(tag, Map.of())
                        .get(stunde);

                Pane zelle = (s != null)
                        ? belegteZelle(s)
                        : leereZelle(tag, stunde);

                grid.add(zelle, col, row);
            }
        }
    }

    // ---------------------------------------------------------------
    // Zellen
    // ---------------------------------------------------------------

    // Leere Zelle – klickbar um neue Stunde anzulegen
    private Pane leereZelle(Wochentag tag, int stundeNummer) {
        VBox zelle = new VBox();
        zelle.setMinHeight(80);
        zelle.setMaxWidth(Double.MAX_VALUE);
        zelle.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-border-radius: 6;" +
                        "-fx-cursor: hand;"
        );

        // Hover-Effekt
        zelle.setOnMouseEntered(e -> zelle.setStyle(
                "-fx-background-color: #f0f4f8;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: #3d5a80;" +
                        "-fx-border-radius: 6;" +
                        "-fx-cursor: hand;"
        ));
        zelle.setOnMouseExited(e -> zelle.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-border-radius: 6;" +
                        "-fx-cursor: hand;"
        ));

        // Klick → Stunde-Bearbeiten-Dialog öffnen
        zelle.setOnMouseClicked(e ->
                StundeBearbeitenDialog.zeige(
                        viewModel, null, tag, stundeNummer
                )
        );

        return zelle;
    }

    // Belegte Zelle – zeigt Fach, Lehrer, Raum
    private Pane belegteZelle(Stunde stunde) {
        VBox zelle = new VBox(2);
        zelle.setMinHeight(80);
        zelle.setMaxWidth(Double.MAX_VALUE);
        zelle.setPadding(new Insets(6));

        // Farbe je nach Status
        String farbe = stunde.isIstAusfall()    ? "#ffcccc" :
                stunde.isIstVertretung() ? "#fff3cc" : "#e8f4ea";
        String rand  = stunde.isIstAusfall()    ? "#cf222e" :
                stunde.isIstVertretung() ? "#bf8700" : "#1a7f37";

        zelle.setStyle(
                "-fx-background-color: " + farbe + ";" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: " + rand + ";" +
                        "-fx-border-radius: 6;" +
                        "-fx-cursor: hand;"
        );

        // Inhalte
        Label lblFach = new Label(
                stunde.getFach() != null ? stunde.getFach().getKuerzel() : "?"
        );
        lblFach.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");

        Label lblLehrer = new Label(
                stunde.getLehrkraft() != null ? stunde.getLehrkraft().getKuerzel() : "?"
        );
        lblLehrer.setStyle("-fx-font-size: 11; -fx-text-fill: #555;");

        Label lblRaum = new Label(
                stunde.getRaum() != null ? stunde.getRaum().getBezeichnung() : "?"
        );
        lblRaum.setStyle("-fx-font-size: 11; -fx-text-fill: #555;");

        // Vertretungs-Badge
        if (stunde.isIstVertretung()) {
            Label badge = new Label("⚠ Vertretung");
            badge.setStyle("-fx-font-size: 10; -fx-text-fill: #bf8700;");
            zelle.getChildren().addAll(lblFach, lblLehrer, lblRaum, badge);
        } else if (stunde.isIstAusfall()) {
            Label badge = new Label("✕ Ausfall");
            badge.setStyle("-fx-font-size: 10; -fx-text-fill: #cf222e;");
            zelle.getChildren().addAll(lblFach, lblLehrer, lblRaum, badge);
        } else {
            zelle.getChildren().addAll(lblFach, lblLehrer, lblRaum);
        }

        // Klick → Stunde bearbeiten
        zelle.setOnMouseClicked(e ->
                StundeBearbeitenDialog.zeige(
                        viewModel, stunde,
                        stunde.getZeitslot().getWochentag(),
                        stunde.getZeitslot().getStundenNummer()
                )
        );

        return zelle;
    }
}