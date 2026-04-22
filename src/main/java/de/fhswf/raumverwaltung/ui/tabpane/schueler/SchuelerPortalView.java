package de.fhswf.raumverwaltung.ui.tabpane.schueler;

import de.fhswf.raumverwaltung.db.entities.Stunde;
import de.fhswf.raumverwaltung.db.entities.Wochentag;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class SchuelerPortalView extends BorderPane {

    private final SchuelerPortalViewModel viewModel;

    // Ansichts-Umschalter
    private final ToggleButton btnHeute    = new ToggleButton("Heute");
    private final ToggleButton btnWoche    = new ToggleButton("Woche");
    private final ToggleButton btnMeineKlasse = new ToggleButton("Meine Klasse");

    // Inhaltsbereich – wird je nach Ansicht ausgetauscht
    private final VBox inhalt = new VBox(8);

    public SchuelerPortalView(SchuelerPortalViewModel viewModel) {
        this.viewModel = viewModel;

        this.setStyle("-fx-background-color: #f5f5f5;");
        this.setTop(buildHeader());
        this.setCenter(buildInhalt());

        // Auf Model-Updates reagieren
        viewModel.getTagesStundenProperty().addListener(
                (obs, o, n) -> {
                    if (btnHeute.isSelected()) zeigeTagsansicht();
                }
        );
        viewModel.getWochenStundenProperty().addListener(
                (obs, o, n) -> {
                    if (btnWoche.isSelected()) zeigeWochenansicht();
                }
        );

        viewModel.laden();
        btnHeute.setSelected(true);
        zeigeTagsansicht();
    }

    // ---------------------------------------------------------------
    // Header
    // ---------------------------------------------------------------

    private VBox buildHeader() {
        Label titel = new Label("Schüler-Portal");
        titel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        // Ansichts-Toggle
        ToggleGroup gruppe = new ToggleGroup();
        btnHeute.setToggleGroup(gruppe);
        btnWoche.setToggleGroup(gruppe);
        btnMeineKlasse.setToggleGroup(gruppe);

        // Stil
        String toggleStyle =
                "-fx-background-radius: 0;" +
                        "-fx-border-color: #3d5a80;" +
                        "-fx-min-width: 120;";
        btnHeute.setStyle(toggleStyle + "-fx-background-radius: 6 0 0 6;");
        btnWoche.setStyle(toggleStyle);
        btnMeineKlasse.setStyle(toggleStyle + "-fx-background-radius: 0 6 6 0;");

        gruppe.selectedToggleProperty().addListener((obs, o, n) -> {
            if (n == btnHeute)       zeigeTagsansicht();
            else if (n == btnWoche)  zeigeWochenansicht();
        });

        HBox toggleBox = new HBox(btnHeute, btnWoche, btnMeineKlasse);

        // Navigation
        Button btnZurueck = new Button("← Zurück");
        Button btnVor     = new Button("Weiter →");
        Label  lblTitel   = new Label();
        lblTitel.textProperty().bind(viewModel.getTitelProperty());
        lblTitel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        btnZurueck.setOnAction(e -> viewModel.navigiereZurueck());
        btnVor.setOnAction(e -> viewModel.navigiereVor());

        HBox navigation = new HBox(12, btnZurueck, lblTitel, btnVor);
        navigation.setAlignment(Pos.CENTER);
        HBox.setHgrow(lblTitel, Priority.ALWAYS);
        lblTitel.setMaxWidth(Double.MAX_VALUE);
        lblTitel.setAlignment(Pos.CENTER);

        VBox header = new VBox(12, titel, toggleBox, navigation);
        header.setPadding(new Insets(16, 16, 12, 16));
        header.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-border-width: 0 0 1 0;"
        );
        return header;
    }

    // ---------------------------------------------------------------
    // Inhaltsbereich
    // ---------------------------------------------------------------

    private ScrollPane buildInhalt() {
        inhalt.setPadding(new Insets(16));
        ScrollPane scroll = new ScrollPane(inhalt);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        return scroll;
    }

    // ---------------------------------------------------------------
    // Tagesansicht – Liste der Stunden mit Ampelfarben
    // ---------------------------------------------------------------

    private void zeigeTagsansicht() {
        inhalt.getChildren().clear();

        var stunden = viewModel.getTagesStundenProperty().get();

        if (stunden == null || stunden.isEmpty()) {
            inhalt.getChildren().add(
                    new Label("Keine Stunden für diesen Tag.")
            );
            return;
        }

        for (Stunde s : stunden) {
            inhalt.getChildren().add(buildStundenKarte(s));
        }
    }

    // ---------------------------------------------------------------
    // Wochenansicht – Grid Mo–Fr
    // ---------------------------------------------------------------

    private void zeigeWochenansicht() {
        inhalt.getChildren().clear();

        GridPane wochenGrid = new GridPane();
        wochenGrid.setHgap(8);
        wochenGrid.setVgap(8);

        // Kopfzeile
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

        // Stunden in Spalten verteilen
        var alleStunden = viewModel.getWochenStundenProperty().get();
        int[] zeilenzaehler = new int[5];
        java.util.Arrays.fill(zeilenzaehler, 1);

        if (alleStunden != null) {
            for (Stunde s : alleStunden) {
                Wochentag tag = s.getZeitslot().getWochentag();
                for (int i = 0; i < wochentage.length; i++) {
                    if (wochentage[i] == tag) {
                        wochenGrid.add(
                                buildStundenKarte(s), i, zeilenzaehler[i]++
                        );
                        break;
                    }
                }
            }
        }

        inhalt.getChildren().add(wochenGrid);
    }

    // ---------------------------------------------------------------
    // Stunden-Karte – Ampelsystem wie im Mockup
    // ---------------------------------------------------------------

    private HBox buildStundenKarte(Stunde stunde) {
        // Farben je nach Status (Ampelsystem aus Mockup)
        String hintergrund;
        String textFarbe;
        String badge = null;

        if (stunde.isIstAusfall()) {
            hintergrund = "#c0392b";  // Rot
            textFarbe   = "white";
            badge       = "✕ Ausfall";
        } else if (stunde.isIstVertretung()) {
            hintergrund = "#f39c12";  // Gelb/Orange
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
        Label lblZeit    = new Label(startzeit + " - " + endzeit);
        lblZeit.setStyle(
                "-fx-min-width: 100;" +
                        "-fx-text-fill: " + (stunde.isIstAusfall() || stunde.isIstVertretung()
                        ? "white" : "#666") + ";" +
                        "-fx-font-size: 12;"
        );

        // Fach + Lehrer/Raum
        String fachName   = stunde.getFach()      != null
                ? stunde.getFach().getBezeichnung()   : "–";
        String lehrerInfo = stunde.getLehrkraft() != null
                ? stunde.getLehrkraft().getName() +
                " (" + stunde.getLehrkraft().getKuerzel() + ")" : "–";
        String raumInfo   = stunde.getRaum()      != null
                ? "| " + stunde.getRaum().getBezeichnung() : "";

        Label lblFach   = new Label(fachName);
        lblFach.setStyle(
                "-fx-font-weight: bold;" +
                        "-fx-font-size: 14;" +
                        "-fx-text-fill: " + textFarbe + ";"
        );

        Label lblDetails = new Label(lehrerInfo + " " + raumInfo);
        lblDetails.setStyle(
                "-fx-font-size: 11;" +
                        "-fx-text-fill: " + (stunde.isIstAusfall() || stunde.isIstVertretung()
                        ? "rgba(255,255,255,0.85)" : "#666") + ";"
        );

        VBox mitte = new VBox(2, lblFach, lblDetails);
        mitte.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(mitte, Priority.ALWAYS);

        HBox karte = new HBox(12, lblZeit, mitte);
        karte.setAlignment(Pos.CENTER_LEFT);
        karte.setPadding(new Insets(12, 16, 12, 16));
        karte.setMaxWidth(Double.MAX_VALUE);

        // Badge rechts (Vertretung / Ausfall)
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

        // Grüner linker Rand bei normalem Unterricht (wie im Mockup)
        if (!stunde.isIstAusfall() && !stunde.isIstVertretung()) {
            stil += "-fx-border-color: transparent transparent transparent #1a7f37;" +
                    "-fx-border-width: 0 0 0 4;" +
                    "-fx-border-radius: 0;";
        }

        karte.setStyle(stil);
        return karte;
    }
}