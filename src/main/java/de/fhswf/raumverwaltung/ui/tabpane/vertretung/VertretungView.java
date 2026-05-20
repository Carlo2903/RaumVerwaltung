package de.fhswf.raumverwaltung.ui.tabpane.vertretung;

import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.db.exception.PlanungException;
import de.fhswf.raumverwaltung.ui.util.EntityStringConverter;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.time.LocalDate;

public class VertretungView extends VBox {

    private final VertretungViewModel viewModel;

    // Sektion 1 – Abwesenheit erfassen
    private final ComboBox<Lehrkraft>      lehrkraftBox  = new ComboBox<>();
    private final DatePicker               vonPicker     = new DatePicker(LocalDate.now());
    private final DatePicker               bisPicker     = new DatePicker(LocalDate.now());
    private final ComboBox<VertretungsGrund> grundBox    = new ComboBox<>();

    // Sektion 2 – Betroffene Stunden
    private final TableView<Stunde>        stundenTable  = new TableView<>();

    // Sektion 3 – Verfügbare Lehrer
    private final FlowPane                 kandidatenPane = new FlowPane();


    public VertretungView(VertretungViewModel viewModel) {
        this.viewModel = viewModel;

        this.setSpacing(16);
        this.setPadding(new Insets(16));
        this.setStyle("-fx-background-color: #f5f5f5;");

        this.getChildren().addAll(
                buildSektion0(),
                buildSektion1(),
                buildSektion2(),
                buildSektion3()
        );

        viewModel.getLehrkraefte().addListener(
                (ListChangeListener<Lehrkraft>) c ->
                        lehrkraftBox.setItems(viewModel.getLehrkraefte())
        );
        viewModel.getBetroffeneStunden().addListener(
                (ListChangeListener<Stunde>) c ->
                        stundenTable.setItems(viewModel.getBetroffeneStunden())
        );
        viewModel.getVerfuegbareLehrer().addListener(
                (ListChangeListener<Lehrkraft>) c ->
                        aktualisiereKandidaten(viewModel.getVerfuegbareLehrer())
        );

        viewModel.laden();
    }


    private VBox buildSektion0() {
        Label titel = new Label("① Übersicht – Abwesenheiten & Vertretungen");
        titel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        TableView<AbwesenheitUebersicht> table = new TableView<>();
        table.setPrefHeight(180);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<AbwesenheitUebersicht, String> colLehrer
                = new TableColumn<>("Lehrkraft");
        TableColumn<AbwesenheitUebersicht, String> colZeitraum
                = new TableColumn<>("Zeitraum");
        TableColumn<AbwesenheitUebersicht, String> colGrund
                = new TableColumn<>("Grund");
        TableColumn<AbwesenheitUebersicht, String> colStatus
                = new TableColumn<>("Vertretungen");

        colLehrer.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().abwesenheit().getLehrkraft() != null
                                ? data.getValue().abwesenheit().getLehrkraft().getName()
                                : "–"
                )
        );
        colZeitraum.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().abwesenheit().getVon() + " – " +
                                data.getValue().abwesenheit().getBis()
                )
        );
        colGrund.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().abwesenheit().getGrund() != null
                                ? data.getValue().abwesenheit().getGrund().toString()
                                : "–"
                )
        );
        colStatus.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getStatusText()
                )
        );

        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText(item);
                AbwesenheitUebersicht row = getTableView()
                        .getItems().get(getIndex());
                setStyle(row.isVollstaendig()
                        ? "-fx-text-fill: #1a7f37; -fx-font-weight: bold;"
                        : "-fx-text-fill: #bf8700; -fx-font-weight: bold;"
                );
            }
        });

        table.getColumns().addAll(colLehrer, colZeitraum, colGrund, colStatus);

        TableColumn<AbwesenheitUebersicht, Void> colLoeschen = new TableColumn<>("");
        colLoeschen.setPrefWidth(100);
        colLoeschen.setCellFactory(col -> new TableCell<>() {
            private final Button btnLoeschen = new Button("🗑 Löschen");

            {
                btnLoeschen.setStyle(
                        "-fx-background-color: #cf222e;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 6;" +
                        "-fx-font-size: 11;"
                );
                btnLoeschen.setOnAction(e -> {
                    AbwesenheitUebersicht row =
                            getTableView().getItems().get(getIndex());

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Abwesenheit von \"" +
                            row.abwesenheit().getLehrkraft().getName() +
                            "\" (" + row.abwesenheit().getVon() +
                            " – " + row.abwesenheit().getBis() + ")" +
                            " komplett löschen?\n\n" +
                            "Alle zugehörigen Vertretungen werden ebenfalls entfernt."
                    );
                    confirm.setTitle("Abwesenheit löschen");
                    confirm.showAndWait().ifPresent(btn -> {
                        if (btn == ButtonType.OK) {
                            viewModel.abwesenheitLoeschen(row.abwesenheit());
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnLoeschen);
            }
        });

        table.getColumns().add(colLoeschen);

        table.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                AbwesenheitUebersicht selected =
                        table.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    viewModel.abwesenheitAuswaehlen(selected);
                }
            }
        });


        table.setItems(viewModel.getAbwesenheitUebersicht());

        VBox sektion = new VBox(8, titel, table);
        sektion.setPadding(new Insets(16));
        sektion.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);"
        );
        return sektion;
    }

    // ---------------------------------------------------------------
    // Sektion 1: Abwesenheit erfassen
    // ---------------------------------------------------------------

    private VBox buildSektion1() {
        Label titel = new Label("② Abwesenheit erfassen");
        titel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        grundBox.getItems().addAll(VertretungsGrund.values());
        grundBox.setValue(VertretungsGrund.KRANK);
        lehrkraftBox.setPromptText("Lehrkraft wählen...");
        lehrkraftBox.setConverter(EntityStringConverter.forLehrkraft());

        Button btnErfassen = new Button("Erfassen");
        btnErfassen.setStyle(
                "-fx-background-color: #3d5a80;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 6;"
        );
        btnErfassen.setOnAction(e -> erfasseAbwesenheit());

        HBox felder = new HBox(12,
                new Label("Lehrkraft:"), lehrkraftBox,
                new Label("Von:"),       vonPicker,
                new Label("Bis:"),       bisPicker,
                new Label("Grund:"),     grundBox,
                btnErfassen
        );
        felder.setAlignment(Pos.CENTER_LEFT);

        VBox sektion = new VBox(8, titel, felder);
        sektion.setPadding(new Insets(16));
        sektion.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);"
        );
        return sektion;
    }

    // ---------------------------------------------------------------
    // Sektion 2: Betroffene Stunden
    // ---------------------------------------------------------------

    private VBox buildSektion2() {
        Label titel = new Label("③ Betroffene Stunden");
        titel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        TableColumn<Stunde, String> colNr    = new TableColumn<>("Std");
        TableColumn<Stunde, String> colZeit  = new TableColumn<>("Zeit");
        TableColumn<Stunde, String> colKlasse = new TableColumn<>("Klasse");
        TableColumn<Stunde, String> colFach  = new TableColumn<>("Fach");
        TableColumn<Stunde, String> colStatus = new TableColumn<>("Status");
        TableColumn<Stunde, Void>   colAktion = new TableColumn<>("Aktion");

        colNr.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(data.getValue().getZeitslot().getStundenNummer())
                )
        );
        colZeit.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getZeitslot().getStartzeit().toString()
                )
        );
        colKlasse.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getKlasse() != null
                                ? data.getValue().getKlasse().getBezeichnung() : "–"
                )
        );
        colFach.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getFach() != null
                                ? data.getValue().getFach().getBezeichnung() : "–"
                )
        );
        colStatus.setCellValueFactory(data -> {
            Stunde s = data.getValue();
            Vertretung v = viewModel.getVertretungenProStunde().get(s.getId());
            boolean isAusfall = (v != null && v.getVertretungsLehrer() == null);
            boolean isVertretung = (v != null && v.getVertretungsLehrer() != null);

            String status = isVertretung ? "✓ Zugewiesen" :
                            isAusfall    ? "✕ Ausfall"    : "⚠ Offen";
            return new javafx.beans.property.SimpleStringProperty(status);
        });

        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle(item.startsWith("✓") ? "-fx-text-fill: #1a7f37; -fx-font-weight: bold;" :
                             item.startsWith("✕") ? "-fx-text-fill: #cf222e; -fx-font-weight: bold;" :
                                                    "-fx-text-fill: #bf8700; -fx-font-weight: bold;"
                    );
                }
            }
        });

        colAktion.setCellFactory(col -> new TableCell<>() {
            private final Button btnZuweisen = new Button("Vertretung →");
            private final Button btnAusfall  = new Button("Als Ausfall markieren");
            private final Button btnLoeschen = new Button("✕ Vertretung entfernen");
            private final Button btnAusfallAufheben = new Button("Ausfall aufheben");

            {
                btnZuweisen.setStyle("-fx-background-color: #3d5a80; -fx-text-fill: white; -fx-background-radius: 6; -fx-font-size: 11;");
                btnAusfall.setStyle("-fx-background-color: transparent; -fx-text-fill: #cf222e; -fx-border-color: #cf222e; -fx-border-radius: 6; -fx-font-size: 11;");
                btnLoeschen.setStyle("-fx-background-color: #cf222e; -fx-text-fill: white; -fx-background-radius: 6; -fx-font-size: 11;");
                btnAusfallAufheben.setStyle("-fx-background-color: #cf222e; -fx-text-fill: white; -fx-background-radius: 6; -fx-font-size: 11;");

                btnZuweisen.setOnAction(e -> {
                    Stunde stunde = getTableView().getItems().get(getIndex());
                    LocalDate datum = vonPicker.getValue();
                    viewModel.stundeAuswaehlen(stunde, datum);
                });

                btnAusfall.setOnAction(e -> {
                    Stunde stunde = getTableView().getItems().get(getIndex());
                    viewModel.setzeAusfall(stunde, true);
                });

                btnLoeschen.setOnAction(e -> {
                    Stunde stunde = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Vertretung für diese Stunde wirklich entfernen?");
                    confirm.showAndWait().ifPresent(btn -> {
                        if (btn == ButtonType.OK) viewModel.loescheVertretung(stunde);
                    });
                });
                
                btnAusfallAufheben.setOnAction(e -> {
                    Stunde stunde = getTableView().getItems().get(getIndex());
                    viewModel.setzeAusfall(stunde, false);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                Stunde stunde = getTableView().getItems().get(getIndex());
                Vertretung v = viewModel.getVertretungenProStunde().get(stunde.getId());
                boolean isAusfall = (v != null && v.getVertretungsLehrer() == null);
                boolean isVertretung = (v != null && v.getVertretungsLehrer() != null);
                
                if (isVertretung) {
                    setGraphic(btnLoeschen);
                } else if (isAusfall) {
                    setGraphic(btnAusfallAufheben);
                } else {
                    HBox box = new HBox(8, btnZuweisen, btnAusfall);
                    box.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(box);
                }
            }
        });

        stundenTable.getColumns().addAll(
                colNr, colZeit, colKlasse, colFach, colStatus, colAktion
        );
        stundenTable.setPrefHeight(200);
        stundenTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox sektion = new VBox(8, titel, stundenTable);
        sektion.setPadding(new Insets(16));
        sektion.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);"
        );
        return sektion;
    }

    // ---------------------------------------------------------------
    // Sektion 3: Verfügbare Vertretungslehrkräfte
    // ---------------------------------------------------------------

    private VBox buildSektion3() {
        Label titel = new Label("④ Verfügbare Vertretungslehrkräfte");
        titel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        kandidatenPane.setHgap(12);
        kandidatenPane.setVgap(12);

        VBox sektion = new VBox(8, titel, kandidatenPane);
        sektion.setPadding(new Insets(16));
        sektion.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);"
        );
        return sektion;
    }

    private void aktualisiereKandidaten(
            javafx.collections.ObservableList<Lehrkraft> lehrer) {
        kandidatenPane.getChildren().clear();

        if (lehrer == null || lehrer.isEmpty()) return;

        for (Lehrkraft lk : lehrer) {
            kandidatenPane.getChildren().add(buildKandidatenKarte(lk));
        }
    }

    private VBox buildKandidatenKarte(Lehrkraft lehrkraft) {
        VBox karte = new VBox(6);
        karte.setPadding(new Insets(12));
        karte.setPrefWidth(180);
        karte.setStyle(
                "-fx-background-color: #eafaf1;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: #1a7f37;" +
                        "-fx-border-radius: 8;"
        );

        String faecher = lehrkraft.getFaecher().stream()
                .map(f -> f.getKuerzel())
                .reduce("", (a, b) -> a.isEmpty() ? b : a + ", ");

        Label lblName = new Label(lehrkraft.getName() + " | " + faecher);
        lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
        lblName.setWrapText(true);

        Button btnZuweisen = new Button("Zuweisen");
        btnZuweisen.setMaxWidth(Double.MAX_VALUE);
        btnZuweisen.setStyle(
                "-fx-background-color: #1a7f37;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 6;"
        );
        btnZuweisen.setOnAction(e -> {
            try {
                viewModel.vertretungZuweisen(lehrkraft, vonPicker.getValue());
            } catch (PlanungException ex) {
                new Alert(Alert.AlertType.WARNING,
                        ex.getKonfliktDetails()).showAndWait();
            }
        });

        karte.getChildren().addAll(lblName, btnZuweisen);
        return karte;
    }

    // ---------------------------------------------------------------
    // Aktionen
    // ---------------------------------------------------------------

    private void erfasseAbwesenheit() {
        if (lehrkraftBox.getValue() == null) {
            new Alert(Alert.AlertType.WARNING,
                    "Bitte eine Lehrkraft auswählen.").showAndWait();
            return;
        }
        if (vonPicker.getValue() == null || bisPicker.getValue() == null) {
            new Alert(Alert.AlertType.WARNING,
                    "Bitte Von- und Bis-Datum auswählen.").showAndWait();
            return;
        }

        try {
            viewModel.abwesenheitErfassen(
                    lehrkraftBox.getValue(),
                    vonPicker.getValue(),
                    bisPicker.getValue(),
                    grundBox.getValue(),
                    ""
            );
        } catch (PlanungException e) {
            new Alert(Alert.AlertType.ERROR,
                    e.getKonfliktDetails()).showAndWait();
        }
    }
}