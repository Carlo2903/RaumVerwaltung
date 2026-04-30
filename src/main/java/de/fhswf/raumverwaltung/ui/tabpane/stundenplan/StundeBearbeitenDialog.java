package de.fhswf.raumverwaltung.ui.tabpane.stundenplan;


import de.fhswf.raumverwaltung.db.entities.*;

import de.fhswf.raumverwaltung.db.exception.PlanungException;

import de.fhswf.raumverwaltung.ui.util.EntityStringConverter;

import javafx.geometry.Insets;

import javafx.scene.control.*;

import javafx.scene.layout.*;


import java.util.List;

import java.util.Optional;


public class StundeBearbeitenDialog {


    public static void zeige(StundenplanViewModel viewModel,

                             Stunde vorhandeneStunde,

                             Wochentag tag,

                             int stundenNummer) {


        Dialog<Void> dialog = new Dialog<>();

        dialog.setTitle(vorhandeneStunde == null

                ? "Stunde anlegen" : "Stunde bearbeiten");


        // ---------------------------------------------------------------

        // ComboBoxen – alle Daten kommen aus ViewModel, kein DAO hier

        // ---------------------------------------------------------------


        ComboBox<Fach>      fachBox   = new ComboBox<>();

        ComboBox<Lehrkraft> lehrerBox = new ComboBox<>();

        ComboBox<Raum>      raumBox   = new ComboBox<>();

        ComboBox<Klasse>    klasseBox = new ComboBox<>();


        // Converter – saubere Anzeige statt toString()

        fachBox.setConverter(EntityStringConverter.forFach());

        lehrerBox.setConverter(EntityStringConverter.forLehrkraft());

        raumBox.setConverter(EntityStringConverter.forRaum());

        klasseBox.setConverter(EntityStringConverter.forKlasse());


        // Daten aus ViewModel laden – kein DAO in der View

        fachBox.getItems().addAll(viewModel.getAlleFaecher());

        raumBox.getItems().addAll(viewModel.getAlleRaeume());

        klasseBox.getItems().addAll(viewModel.getKlassenProperty().get());


        // Lehrkraft – anfangs leer, erst nach Fach-Auswahl befüllen

        lehrerBox.setPromptText("Erst Fach wählen...");

        lehrerBox.setDisable(true);


        // Fach gewählt → nur passende Lehrkräfte anzeigen

        // Filterlogik liegt im ViewModel – nicht hier

        fachBox.setOnAction(e -> {

            Fach gewaehltesFach = fachBox.getValue();

            lehrerBox.getItems().clear();

            lehrerBox.setValue(null);


            if (gewaehltesFach != null) {

                List<Lehrkraft> gefiltert =

                        viewModel.findeLehrkraefteNachFach(gewaehltesFach);

                lehrerBox.getItems().addAll(gefiltert);

                lehrerBox.setDisable(false);

                lehrerBox.setPromptText(

                        gefiltert.isEmpty()

                                ? "Keine Lehrkraft verfügbar"

                                : "Lehrkraft wählen..."

                );

            } else {

                lehrerBox.setDisable(true);

                lehrerBox.setPromptText("Erst Fach wählen...");

            }

        });


        // ---------------------------------------------------------------

        // Konflikt-Hinweis

        // ---------------------------------------------------------------


        Label konfliktLabel = new Label();

        konfliktLabel.setStyle("-fx-text-fill: #cf222e;");

        konfliktLabel.setVisible(false);

        konfliktLabel.setManaged(false);


        // ---------------------------------------------------------------

        // Formular

        // ---------------------------------------------------------------


        GridPane form = new GridPane();

        form.setHgap(12);

        form.setVgap(8);

        form.setPadding(new Insets(16));


        fachBox.setMaxWidth(Double.MAX_VALUE);

        lehrerBox.setMaxWidth(Double.MAX_VALUE);

        raumBox.setMaxWidth(Double.MAX_VALUE);

        klasseBox.setMaxWidth(Double.MAX_VALUE);


        form.add(new Label("Fach:"),      0, 0); form.add(fachBox,      1, 0);

        form.add(new Label("Lehrkraft:"), 0, 1); form.add(lehrerBox,    1, 1);

        form.add(new Label("Raum:"),      0, 2); form.add(raumBox,      1, 2);

        form.add(new Label("Klasse:"),    0, 3); form.add(klasseBox,    1, 3);

        form.add(konfliktLabel,           0, 4, 2, 1);


        GridPane.setHgrow(fachBox,   Priority.ALWAYS);

        GridPane.setHgrow(lehrerBox, Priority.ALWAYS);

        GridPane.setHgrow(raumBox,   Priority.ALWAYS);

        GridPane.setHgrow(klasseBox, Priority.ALWAYS);


        dialog.getDialogPane().setContent(form);

        dialog.getDialogPane().setPrefWidth(400);


        // ---------------------------------------------------------------

        // Buttons

        // ---------------------------------------------------------------


        ButtonType btnSpeichern = new ButtonType("Speichern",

                ButtonBar.ButtonData.OK_DONE);

        ButtonType btnAbbrechen = new ButtonType("Abbrechen",

                ButtonBar.ButtonData.CANCEL_CLOSE);


        dialog.getDialogPane().getButtonTypes().addAll(btnSpeichern, btnAbbrechen);


        // Löschen-Button nur bei bestehender Stunde

        if (vorhandeneStunde != null) {
            ButtonType btnLoeschen = new ButtonType("Löschen",
                    ButtonBar.ButtonData.LEFT);
            dialog.getDialogPane().getButtonTypes().add(btnLoeschen);

            Button loeschenBtn = (Button) dialog.getDialogPane()
                    .lookupButton(btnLoeschen);
            loeschenBtn.setStyle(
                    "-fx-background-color: #cf222e; -fx-text-fill: white;"
            );

            loeschenBtn.setOnAction(e -> {
                // NEU: Prüfen ob Vertretung vorhanden
                boolean hatVertretung = vorhandeneStunde.isIstVertretung();

                if (hatVertretung) {
                    // Warnung anzeigen – Benutzer muss bestätigen
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Diese Stunde hat eine zugewiesene Vertretung. " +
                                    "Beim Löschen wird die Vertretung ebenfalls entfernt. " +
                                    "Trotzdem löschen?");
                    confirm.showAndWait().ifPresent(btn -> {
                        if (btn == ButtonType.OK) {
                            viewModel.stundeLoeschen(vorhandeneStunde);
                            dialog.close();
                        }
                    });
                } else {
                    // Keine Vertretung – direkt löschen
                    viewModel.stundeLoeschen(vorhandeneStunde);
                    dialog.close();
                }
            });
        }


        // Speichern-Button – Validierung über Konflikt-Label

        Button speichernBtn = (Button) dialog.getDialogPane()

                .lookupButton(btnSpeichern);


        // Dialog nicht automatisch schließen – erst nach erfolgreicher Validierung

        speichernBtn.addEventFilter(

                javafx.event.ActionEvent.ACTION, event -> {

                    event.consume(); // verhindert automatisches Schließen


                    // Validierung

                    if (fachBox.getValue() == null) {

                        zeigeKonflikt(konfliktLabel, "Bitte ein Fach auswählen.");

                        return;

                    }

                    if (lehrerBox.getValue() == null) {

                        zeigeKonflikt(konfliktLabel, "Bitte eine Lehrkraft auswählen.");

                        return;

                    }

                    if (raumBox.getValue() == null) {

                        zeigeKonflikt(konfliktLabel, "Bitte einen Raum auswählen.");

                        return;

                    }

                    if (klasseBox.getValue() == null) {

                        zeigeKonflikt(konfliktLabel, "Bitte eine Klasse auswählen.");

                        return;

                    }


                    // Zeitslot für diesen Tag + Stundennummer suchen

                    Optional<Zeitslot> slot = viewModel.findeZeitslot(tag, stundenNummer);

                    if (slot.isEmpty()) {

                        zeigeKonflikt(konfliktLabel,

                                "Kein Zeitslot für diesen Tag gefunden. " +

                                        "Bitte Zeitslots anlegen.");

                        return;

                    }


                    // Stunde zusammenbauen

                    Stunde stunde = vorhandeneStunde != null

                            ? vorhandeneStunde

                            : new Stunde();


                    stunde.setFach(fachBox.getValue());

                    stunde.setLehrkraft(lehrerBox.getValue());

                    stunde.setRaum(raumBox.getValue());

                    stunde.setKlasse(klasseBox.getValue());

                    stunde.setZeitslot(slot.get());

                    stunde.setStundenplan(viewModel.getAktuellerPlan());


                    // Konfliktprüfung + Speichern über ViewModel

                    try {

                        viewModel.stundeSetzen(stunde);

                        dialog.close(); // nur bei Erfolg schließen

                    } catch (PlanungException e) {

                        zeigeKonflikt(konfliktLabel, e.getKonfliktDetails());

                    }

                }

        );


        // ---------------------------------------------------------------

        // Vorhandene Stunde vorbelegen

        // ---------------------------------------------------------------


        if (vorhandeneStunde != null) {

            // Fach zuerst setzen – fachBox.setOnAction füllt lehrerBox automatisch

            fachBox.setValue(vorhandeneStunde.getFach());

            lehrerBox.setValue(vorhandeneStunde.getLehrkraft());

            raumBox.setValue(vorhandeneStunde.getRaum());

            klasseBox.setValue(vorhandeneStunde.getKlasse());

        }


        dialog.showAndWait();

    }




    // ---------------------------------------------------------------

    // Hilfsmethode

    // ---------------------------------------------------------------


    private static void zeigeKonflikt(Label label, String text) {

        label.setText("⚠ " + text);

        label.setVisible(true);

        label.setManaged(true);

    }

}