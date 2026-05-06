package de.fhswf.raumverwaltung.ui.tabpane.stundenplan;

import de.fhswf.raumverwaltung.MainApp;
import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.ui.util.EntityStringConverter;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageOrientation;
import javafx.print.Printer;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.Map;

public class StundenplanRasterView extends BorderPane {

    private final StundenplanViewModelInterface viewModel;
    private final boolean                       readOnly;

    private static final Wochentag[] TAGE = {
            Wochentag.MONTAG, Wochentag.DIENSTAG, Wochentag.MITTWOCH,
            Wochentag.DONNERSTAG, Wochentag.FREITAG
    };

    private static final int STUNDEN_PRO_TAG = 6;

    private final GridPane grid = new GridPane();

    public StundenplanRasterView(StundenplanViewModelInterface viewModel,
                                 boolean readOnly) {
        this.viewModel = viewModel;
        this.readOnly  = readOnly;

        this.setTop(buildHeader());
        this.setCenter(buildGrid());

        viewModel.getStundenListe().addListener(
                (ListChangeListener<Stunde>) change ->
                        aktualisiereGrid(viewModel.getGridProperty().get())
        );

        viewModel.getGridProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) aktualisiereGrid(newVal);
        });

        viewModel.laden();
    }

    // ---------------------------------------------------------------
    // Header: Titel + Klassen-Filter
    // ---------------------------------------------------------------

    private HBox buildHeader() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(12, 16, 12, 16));
        header.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-border-width: 0 0 1 0;"
        );

        if (readOnly) {
            // Lehrer: nur Titel, kein Filter
            Label titel = new Label("Mein Stundenplan");
            titel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
            header.getChildren().add(titel);

        } else {
            // Admin: Klassen-Filter
            Label titel = new Label("Stundenplan");
            titel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

            Label lblKlasse = new Label("Klasse:");

            ComboBox<Klasse> klasseBox = new ComboBox<>();
            klasseBox.setPromptText("Klasse wählen...");
            klasseBox.setConverter(EntityStringConverter.forKlasse());

            viewModel.getKlassenProperty().addListener(
                    (obs, oldVal, newVal) -> {
                        if (newVal != null) {
                            javafx.application.Platform.runLater(() -> {
                                klasseBox.getSelectionModel().clearSelection();
                                klasseBox.setItems(newVal);
                            });
                        }
                    }
            );

            klasseBox.setOnAction(e ->
                    viewModel.filterNachKlasse(
                            klasseBox.getSelectionModel().getSelectedItem()
                    )
            );

            Button btnExport = new Button("📄 PDF exportieren");
            btnExport.setStyle(
                    "-fx-background-color: #3d5a80;" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 6;" +
                            "-fx-padding: 6 12 6 12;"
            );
            btnExport.setOnAction(e -> exportierePdf());

            // Spacer damit Button rechts bleibt
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            header.getChildren().addAll(
                    titel, new Separator(), lblKlasse, klasseBox,
                    spacer, btnExport // NEU
            );
        }

        return header;
    }


    private void exportierePdf() {
        javafx.print.PrinterJob job =
                javafx.print.PrinterJob.createPrinterJob();

        if (job == null) {
            new Alert(Alert.AlertType.ERROR,
                    "Kein Drucker verfügbar.").showAndWait();
            return;
        }

        boolean drucken = job.showPrintDialog(MainApp.primaryStage);

        if (drucken) {
            javafx.print.PageLayout pageLayout = job.getPrinter()
                    .createPageLayout(
                            javafx.print.Paper.A4,
                            PageOrientation.PORTRAIT,
                            Printer.MarginType.EQUAL
                    );

            double breite = this.getWidth();
            double hoehe  = this.getHeight();

            if (breite == 0 || hoehe == 0) {
                new Alert(Alert.AlertType.WARNING,
                        "Stundenplan noch nicht vollständig geladen.")
                        .showAndWait();
                return;
            }

            double scaleX = pageLayout.getPrintableWidth()  / breite;
            double scaleY = pageLayout.getPrintableHeight() / hoehe;
            double scale  = Math.min(scaleX, scaleY);

            javafx.scene.transform.Scale transform =
                    new javafx.scene.transform.Scale(scale, scale);

            this.getTransforms().add(transform);

            boolean erfolg = job.printPage(pageLayout, this);

            this.getTransforms().remove(transform);

            if (erfolg) {
                job.endJob();
            } else {
                new Alert(Alert.AlertType.ERROR,
                        "Drucken fehlgeschlagen.").showAndWait();
            }
        }
    }

    // ---------------------------------------------------------------
    // Grid aufbauen
    // ---------------------------------------------------------------

    private ScrollPane buildGrid() {
        grid.setHgap(4);
        grid.setVgap(4);
        grid.setPadding(new Insets(12));
        grid.setStyle("-fx-background-color: #f5f5f5;");

        ColumnConstraints colZeit = new ColumnConstraints();
        colZeit.setPrefWidth(60);
        colZeit.setMinWidth(40);
        grid.getColumnConstraints().add(colZeit);

        for (int i = 0; i < TAGE.length; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setHgrow(Priority.ALWAYS);
            col.setMinWidth(80);
            col.setFillWidth(true);
            grid.getColumnConstraints().add(col);
        }

        // Kopfzeile
        grid.add(new Label(""), 0, 0);
        for (int i = 0; i < TAGE.length; i++) {
            Label tagLabel = new Label(TAGE[i].name().substring(0, 2));
            tagLabel.setMaxWidth(Double.MAX_VALUE);
            tagLabel.setAlignment(Pos.CENTER);
            tagLabel.setPadding(new Insets(6));
            tagLabel.setStyle(
                    "-fx-font-weight: bold;" +
                            "-fx-font-size: 13;" +
                            "-fx-background-color: #3d5a80;" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 6;"
            );
            grid.add(tagLabel, i + 1, 0);
        }

        // Zeilen
        for (int stunde = 1; stunde <= STUNDEN_PRO_TAG; stunde++) {
            Label stdLabel = new Label(stunde + ".");
            stdLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
            stdLabel.setPadding(new Insets(4));
            grid.add(stdLabel, 0, stunde);

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
    // Grid aktualisieren
    // ---------------------------------------------------------------

    private void aktualisiereGrid(Map<Wochentag, Map<Integer, Stunde>> stundenGrid) {
        if (stundenGrid == null) return;

        for (int stunde = 1; stunde <= STUNDEN_PRO_TAG; stunde++) {
            for (int tagIdx = 0; tagIdx < TAGE.length; tagIdx++) {
                Wochentag tag = TAGE[tagIdx];

                final int col = tagIdx + 1;
                final int row = stunde;

                grid.getChildren().removeIf(node ->
                        GridPane.getColumnIndex(node) != null &&
                                GridPane.getColumnIndex(node) == col &&
                                GridPane.getRowIndex(node) != null &&
                                GridPane.getRowIndex(node) == row
                );

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
    // Leere Zelle
    // ---------------------------------------------------------------

    private Pane leereZelle(Wochentag tag, int stundeNummer) {
        VBox zelle = new VBox();
        zelle.setMinHeight(80);
        zelle.setMaxWidth(Double.MAX_VALUE);

        String normalStyle =
                "-fx-background-color: white;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-border-radius: 6;";

        if (readOnly) {
            // Lehrer/Schüler: keine Interaktion
            zelle.setStyle(normalStyle);
        } else {
            // Admin: klickbar
            zelle.setStyle(normalStyle + "-fx-cursor: hand;");
            zelle.setOnMouseEntered(e -> zelle.setStyle(
                    "-fx-background-color: #f0f4f8;" +
                            "-fx-background-radius: 6;" +
                            "-fx-border-color: #3d5a80;" +
                            "-fx-border-radius: 6;" +
                            "-fx-cursor: hand;"
            ));
            zelle.setOnMouseExited(e ->
                    zelle.setStyle(normalStyle + "-fx-cursor: hand;")
            );
            zelle.setOnMouseClicked(e -> {
                if (viewModel.getAktuellerPlan() == null) {
                    new Alert(Alert.AlertType.WARNING,
                            "Kein aktiver Stundenplan gefunden. " +
                                    "Bitte zuerst ein Schuljahr anlegen.")
                            .showAndWait();
                    return;
                }
                viewModel.zeigeBearbeitenDialog(null, tag, stundeNummer);
            });
        }

        return zelle;
    }

    // ---------------------------------------------------------------
    // Belegte Zelle
    // ---------------------------------------------------------------

    private Pane belegteZelle(Stunde stunde) {
        String farbe = stunde.isIstAusfall()    ? "#ffcccc" :
                stunde.isIstVertretung() ? "#fff3cc" : "#e8f4ea";
        String rand  = stunde.isIstAusfall()    ? "#cf222e" :
                stunde.isIstVertretung() ? "#bf8700" : "#1a7f37";

        String lehrerText;
        if (stunde.isIstVertretung()) {
            lehrerText = viewModel.getVertretungslehrerName(stunde) + " (V)";
        } else {
            lehrerText = stunde.getLehrkraft() != null
                    ? stunde.getLehrkraft().getKuerzel() : "?";
        }

        Label lblFach = new Label(stunde.getFach() != null
                ? stunde.getFach().getKuerzel() : "?");
        lblFach.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");

        Label lblLehrer = new Label(lehrerText);
        lblLehrer.setStyle("-fx-font-size: 11; -fx-text-fill: #555;");

        Label lblRaum = new Label(stunde.getRaum() != null
                ? stunde.getRaum().getBezeichnung() : "?");
        lblRaum.setStyle("-fx-font-size: 11; -fx-text-fill: #555;");

        VBox inhalt = new VBox(2, lblFach, lblLehrer, lblRaum);
        inhalt.setPadding(new Insets(6));
        inhalt.setMaxWidth(Double.MAX_VALUE);
        inhalt.setMaxHeight(Double.MAX_VALUE);

        if (stunde.isIstVertretung()) {
            Label badge = new Label("⚠ Vertretung");
            badge.setStyle("-fx-font-size: 10; -fx-text-fill: #bf8700;");
            inhalt.getChildren().add(badge);
        } else if (stunde.isIstAusfall()) {
            Label badge = new Label("✕ Ausfall");
            badge.setStyle("-fx-font-size: 10; -fx-text-fill: #cf222e;");
            inhalt.getChildren().add(badge);
        }

        StackPane zelle = new StackPane(inhalt);
        zelle.setMinHeight(80);
        zelle.setMaxWidth(Double.MAX_VALUE);

        String basisStyle =
                "-fx-background-color: " + farbe + ";" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: " + rand + ";" +
                        "-fx-border-radius: 6;";

        zelle.setStyle(basisStyle + (readOnly ? "" : " -fx-cursor: hand;"));

        // Zähler oben rechts
        if (stunde.getFach() != null && stunde.getKlasse() != null) {
            int aktuell = viewModel.getStundenZaehler(
                    stunde.getKlasse(), stunde.getFach()
            );
            int max = stunde.getFach().getWochenstundenProKlasse();

            String zaehlerFarbe = aktuell > max  ? "#cf222e" :
                    aktuell == max ? "#1a7f37" :
                            "#bf8700";

            Label lblZaehler = new Label(aktuell + "/" + max);
            lblZaehler.setStyle(
                    "-fx-font-size: 10;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: " + zaehlerFarbe + ";" +
                            "-fx-padding: 2 4 2 4;" +
                            "-fx-background-color: rgba(255,255,255,0.7);" +
                            "-fx-background-radius: 4;"
            );
            StackPane.setAlignment(lblZaehler, Pos.TOP_RIGHT);
            StackPane.setMargin(lblZaehler, new Insets(4, 4, 0, 0));
            zelle.getChildren().add(lblZaehler);
        }

        if (stunde.getLehrkraft() != null) {
            int aktuell = viewModel.getLehrkraftStunden(stunde.getLehrkraft());
            int max     = stunde.getLehrkraft().getSollStunden();

            if (aktuell > max) {
                // Überschritten – roter Badge
                Label lblWarnung = new Label(
                        "⚠ " + aktuell + "/" + max + " Std"
                );
                lblWarnung.setStyle(
                        "-fx-font-size: 9;" +
                                "-fx-text-fill: #cf222e;" +
                                "-fx-font-weight: bold;"
                );
                inhalt.getChildren().add(lblWarnung);
            }
        }

        if (readOnly) {
            zelle.setOnMouseEntered(e -> zelle.setStyle(
                    "-fx-background-color: derive(" + farbe + ", -10%);" +
                            "-fx-background-radius: 6;" +
                            "-fx-border-color: derive(" + rand + ", -20%);" +
                            "-fx-border-radius: 6;" +
                            "-fx-cursor: hand;"
            ));
            zelle.setOnMouseExited(e ->
                    zelle.setStyle(basisStyle + " -fx-cursor: hand;")
            );
            zelle.setOnMouseClicked(e -> {
                de.fhswf.raumverwaltung.ui.tabpane.schueler.StundenDetailDialog.zeige(
                        stunde,
                        null,
                        stunde.isIstVertretung(),
                        stunde.isIstVertretung() ? viewModel.getVertretungslehrerName(stunde) : null,
                        stunde.isIstAusfall()
                );
            });
        } else {
            // Admin: Hover + Klick zum Bearbeiten
            zelle.setOnMouseEntered(e -> zelle.setStyle(
                    "-fx-background-color: derive(" + farbe + ", -10%);" +
                            "-fx-background-radius: 6;" +
                            "-fx-border-color: derive(" + rand + ", -20%);" +
                            "-fx-border-radius: 6;" +
                            "-fx-cursor: hand;"
            ));
            zelle.setOnMouseExited(e ->
                    zelle.setStyle(basisStyle + " -fx-cursor: hand;")
            );
            zelle.setOnMouseClicked(e -> {
                viewModel.zeigeBearbeitenDialog(
                        stunde,
                        stunde.getZeitslot().getWochentag(),
                        stunde.getZeitslot().getStundenNummer()
                );
            });
        }

        return zelle;
    }
}