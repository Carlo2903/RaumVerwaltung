package de.fhswf.raumverwaltung.ui.tabpane.schueler;

import de.fhswf.raumverwaltung.db.entities.Stunde;
import de.fhswf.raumverwaltung.db.entities.Wochentag;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class SchuelerPortalView extends BorderPane {

    private final SchuelerPortalViewModel viewModel;

    // Ansichts-Umschalter
    private final ToggleButton btnHeute = new ToggleButton("Heute");
    private final ToggleButton btnWoche = new ToggleButton("Woche");

    // Navigation – einmal erstellt, nicht in aktualisiereNavigation()
    private final Button btnTagZurueck   = new Button("← Zurück");
    private final Button btnTagVor       = new Button("Weiter →");
    private final Button btnWocheZurueck = new Button("← Vorherige Woche");
    private final Button btnWocheVor     = new Button("Nächste Woche →");

    private final Label lblTitel = new Label();

    private final HBox navigationBox = new HBox(12);

    private final VBox inhalt = new VBox(8);

    public SchuelerPortalView(SchuelerPortalViewModel viewModel) {
        this.viewModel = viewModel;

        // Titel einmal binden
        lblTitel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        lblTitel.setMaxWidth(Double.MAX_VALUE);
        lblTitel.setAlignment(Pos.CENTER);
        HBox.setHgrow(lblTitel, Priority.ALWAYS);

        this.setStyle("-fx-background-color: #f5f5f5;");
        this.setTop(buildHeader());
        this.setCenter(buildInhalt());

        viewModel.getTagesStundenProperty().addListener(
                (ListChangeListener<Stunde>) c -> {
                    if (btnHeute.isSelected()) zeigeTagsansicht();
                }
        );
        viewModel.getWochenStundenProperty().addListener(
                (ListChangeListener<Stunde>) c -> {
                    if (btnWoche.isSelected()) zeigeWochenansicht();
                }
        );



        viewModel.laden();
        btnHeute.setSelected(true);
        aktualisiereNavigation();
        zeigeTagsansicht();
    }

    // ---------------------------------------------------------------
    // Header
    // ---------------------------------------------------------------

    private VBox buildHeader() {
        Label titel = new Label("Schüler-Portal");
        titel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        ToggleGroup gruppe = new ToggleGroup();
        btnHeute.setToggleGroup(gruppe);
        btnWoche.setToggleGroup(gruppe);

        btnHeute.setStyle(
                "-fx-background-radius: 6 0 0 6;" +
                        "-fx-border-color: #3d5a80;" +
                        "-fx-min-width: 120;"
        );
        btnWoche.setStyle(
                "-fx-background-radius: 0 6 6 0;" +
                        "-fx-border-color: #3d5a80;" +
                        "-fx-min-width: 120;"
        );

        // Toggle-Wechsel → Navigation + Ansicht aktualisieren
        gruppe.selectedToggleProperty().addListener((obs, o, n) -> {
            aktualisiereNavigation();
            if (n == btnHeute)      zeigeTagsansicht();
            else if (n == btnWoche) zeigeWochenansicht();
        });

        gruppe.selectedToggleProperty().addListener((obs, o, n) -> {
            aktualisiereNavigation();
            if (n == btnHeute) {
                // Tages-Titel binden
                lblTitel.textProperty().bind(viewModel.getTagesTitelProperty());
                zeigeTagsansicht();
            } else if (n == btnWoche) {
                // Wochen-Titel binden
                lblTitel.textProperty().bind(viewModel.getWochenTitelProperty());
                zeigeWochenansicht();
            }
        });


        lblTitel.textProperty().bind(viewModel.getTagesTitelProperty());


        viewModel.getTagesStundenProperty().addListener(
                (ListChangeListener<Stunde>) c -> {
                    if (btnHeute.isSelected()) zeigeTagsansicht();
                }
        );
        viewModel.getWochenStundenProperty().addListener(
                (ListChangeListener<Stunde>) c -> {
                    if (btnWoche.isSelected()) zeigeWochenansicht();
                }
        );

        HBox toggleBox = new HBox(btnHeute, btnWoche);

        navigationBox.setAlignment(Pos.CENTER);

        VBox header = new VBox(12, titel, toggleBox, navigationBox);
        header.setPadding(new Insets(16, 16, 12, 16));
        header.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-border-width: 0 0 1 0;"
        );
        return header;
    }


    private void aktualisiereNavigation() {
        navigationBox.getChildren().clear();

        if (btnHeute.isSelected()) {
            // Nur viewModel aufrufen – Observer aktualisiert die View
            btnTagZurueck.setOnAction(e -> viewModel.navigiereZurueck());
            btnTagVor.setOnAction(e -> viewModel.navigiereVor());
            navigationBox.getChildren().addAll(btnTagZurueck, lblTitel, btnTagVor);
        } else {
            // Woche vor/zurück – nicht Tag für Tag
            btnWocheZurueck.setOnAction(e -> viewModel.navigiereWocheZurueck());
            btnWocheVor.setOnAction(e -> viewModel.navigiereWocheVor());
            navigationBox.getChildren().addAll(btnWocheZurueck, lblTitel, btnWocheVor);
        }
    }


    private ScrollPane buildInhalt() {
        inhalt.setPadding(new Insets(16));
        ScrollPane scroll = new ScrollPane(inhalt);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        return scroll;
    }

    // ---------------------------------------------------------------
    // Tagesansicht
    // ---------------------------------------------------------------

    private void zeigeTagsansicht() {
        inhalt.getChildren().clear();

        // Direkt aus ObservableList lesen
        var stunden = viewModel.getTagesStundenProperty();

        if (stunden.isEmpty()) {
            inhalt.getChildren().add(new Label("Keine Stunden für diesen Tag."));
            return;
        }

        LocalDate datum = viewModel.getAktuellesDatum();
        for (Stunde s : stunden) {
            inhalt.getChildren().add(buildStundenKarte(s, datum));
        }
    }

    // ---------------------------------------------------------------
    // Wochenansicht
    // ---------------------------------------------------------------

    private void zeigeWochenansicht() {
        inhalt.getChildren().clear();

        GridPane wochenGrid = new GridPane();
        wochenGrid.setHgap(8);
        wochenGrid.setVgap(8);

        String[] tage = {"MO", "DI", "MI", "DO", "FR"};
        Wochentag[] wochentage = {
                Wochentag.MONTAG, Wochentag.DIENSTAG,
                Wochentag.MITTWOCH, Wochentag.DONNERSTAG,
                Wochentag.FREITAG
        };

        for (int i = 0; i < tage.length; i++) {
            Label kopf = new Label(tage[i]);
            kopf.setMaxWidth(Double.MAX_VALUE);
            kopf.setAlignment(Pos.CENTER);
            kopf.setPadding(new Insets(6));
            kopf.setStyle(
                    "-fx-font-weight: bold;" +
                            "-fx-background-color: #3d5a80;" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 6;"
            );
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            cc.setMinWidth(100);
            wochenGrid.getColumnConstraints().add(cc);
            wochenGrid.add(kopf, i, 0);
        }


        var alleStunden = viewModel.getWochenStundenProperty();
        int[] zeilenzaehler = new int[5];
        java.util.Arrays.fill(zeilenzaehler, 1);

        LocalDate montag = viewModel.getAktuellesDatum().with(DayOfWeek.MONDAY);

        for (Stunde s : alleStunden) {
            Wochentag tag = s.getZeitslot().getWochentag();
            for (int i = 0; i < wochentage.length; i++) {
                if (wochentage[i] == tag) {
                    LocalDate tagDatum = montag.plusDays(i);
                    wochenGrid.add(
                            buildStundenKarte(s, tagDatum),
                            i, zeilenzaehler[i]++
                    );
                    break;
                }
            }
        }

        inhalt.getChildren().add(wochenGrid);
    }


    private HBox buildStundenKarte(Stunde stunde, LocalDate datum) {
        // Vertretung oder Ausfall nur wenn Datum übereinstimmt
        boolean hatVertretung = viewModel.istVertretungAmDatum(stunde, datum);
        boolean istAusfall    = viewModel.istAusfallAmDatum(stunde, datum);

        String hintergrund;
        String textFarbe;
        String badge;

        if (istAusfall) {
            hintergrund = "#c0392b";
            textFarbe   = "white";
            badge       = "✕ Ausfall";
        } else if (hatVertretung) {
            hintergrund = "#f39c12";
            textFarbe   = "white";
            badge       = "⚠ Vertretung";
        } else {
            hintergrund = "white";
            textFarbe   = "#2d3436";
            badge       = null;
        }

        // Zeit
        String startzeit = stunde.getZeitslot().getStartzeit().toString();
        String endzeit   = stunde.getZeitslot().getEndzeit().toString();
        Label lblZeit = new Label(startzeit + " - " + endzeit);
        lblZeit.setStyle(
                "-fx-min-width: 100;" +
                        "-fx-text-fill: " + (istAusfall || hatVertretung
                        ? "white" : "#666") + ";" +
                        "-fx-font-size: 12;"
        );

        // Fach
        String fachName = stunde.getFach() != null
                ? stunde.getFach().getBezeichnung() : "–";

        // Raum
        String raumInfo = stunde.getRaum() != null
                ? "| " + stunde.getRaum().getBezeichnung() : "";

        // Lehrer – datum-abhängig
        String lehrerInfo;
        if (hatVertretung) {
            lehrerInfo = viewModel.getVertretungslehrerName(stunde, datum)
                    + " (Vertretung)";
        } else {
            lehrerInfo = stunde.getLehrkraft() != null
                    ? stunde.getLehrkraft().getName() +
                    " (" + stunde.getLehrkraft().getKuerzel() + ")"
                    : "–";
        }

        Label lblFach = new Label(fachName);
        lblFach.setStyle(
                "-fx-font-weight: bold;" +
                        "-fx-font-size: 14;" +
                        "-fx-text-fill: " + textFarbe + ";"
        );

        Label lblDetails = new Label(lehrerInfo + " " + raumInfo);
        lblDetails.setStyle(
                "-fx-font-size: 11;" +
                        "-fx-text-fill: " + (istAusfall || hatVertretung
                        ? "rgba(255,255,255,0.85)" : "#666") + ";"
        );

        VBox mitte = new VBox(2, lblFach, lblDetails);
        mitte.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(mitte, Priority.ALWAYS);

        HBox karte = new HBox(12, lblZeit, mitte);
        karte.setAlignment(Pos.CENTER_LEFT);
        karte.setPadding(new Insets(12, 16, 12, 16));
        karte.setMaxWidth(Double.MAX_VALUE);

        if (badge != null) {
            Label lblBadge = new Label(badge);
            lblBadge.setStyle(
                    "-fx-font-size: 12;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: white;"
            );
            karte.getChildren().add(lblBadge);
        }

        String stil =
                "-fx-background-color: " + hintergrund + ";" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 6, 0, 0, 2);";

        if (!istAusfall && !hatVertretung) {
            stil +=
                    "-fx-border-color: transparent transparent transparent #1a7f37;" +
                            "-fx-border-width: 0 0 0 4;" +
                            "-fx-border-radius: 0;";
        }
        karte.setOnMouseClicked(e -> StundenDetailDialog.zeige(
                stunde,
                datum,
                hatVertretung,
                viewModel.getVertretungslehrerName(stunde, datum),
                istAusfall
        ));
        karte.setStyle(stil);
        return karte;
    }
}