package de.fhswf.raumverwaltung.ui.tabpane.lehrkraft;

import de.fhswf.raumverwaltung.db.entities.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SperrzeitDialog {

    private static final Wochentag[] WOCHENTAGE = {
            Wochentag.MONTAG, Wochentag.DIENSTAG, Wochentag.MITTWOCH,
            Wochentag.DONNERSTAG, Wochentag.FREITAG
    };

    // Gibt gesperrte Zeitslots zurück – oder empty wenn abgebrochen
    public static Optional<List<Zeitslot>> zeige(
            Lehrkraft lehrkraft,
            List<Zeitslot> alleZeitslots,
            List<Sperrzeit> aktuelleSperrzeiten) {

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Sperrzeiten – " + lehrkraft.getName());
        dialog.getDialogPane().setPrefWidth(450);

        Map<String, CheckBox> checkboxen = new HashMap<>();

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setPadding(new Insets(16));

        // Kopfzeile
        String[] tage = {"", "MO", "DI", "MI", "DO", "FR"};
        for (int i = 0; i < tage.length; i++) {
            Label lbl = new Label(tage[i]);
            lbl.setStyle("-fx-font-weight: bold;");
            lbl.setPrefWidth(60);
            lbl.setAlignment(Pos.CENTER);
            grid.add(lbl, i, 0);
        }

        // Zeilen: Stunden 1–6
        for (int stunde = 1; stunde <= 6; stunde++) {
            Label stdLabel = new Label(stunde + ". Stunde");
            stdLabel.setStyle("-fx-font-weight: bold;");
            grid.add(stdLabel, 0, stunde);

            for (int tagIdx = 0; tagIdx < WOCHENTAGE.length; tagIdx++) {
                CheckBox cb = new CheckBox();
                cb.setPrefWidth(60);
                cb.setAlignment(Pos.CENTER);

                String key = WOCHENTAGE[tagIdx].name() + "_" + stunde;
                checkboxen.put(key, cb);
                grid.add(cb, tagIdx + 1, stunde);
            }
        }

        // Aktuelle Sperrzeiten markieren
        aktuelleSperrzeiten.forEach(sz -> {
            String key = sz.getZeitslot().getWochentag().name() + "_" +
                    sz.getZeitslot().getStundenNummer();
            CheckBox cb = checkboxen.get(key);
            if (cb != null) cb.setSelected(true);
        });

        // Hinweis
        Label hinweis = new Label(
                "✓ = Lehrkraft ist zu diesem Zeitslot NICHT verfügbar"
        );
        hinweis.setStyle("-fx-font-size: 11; -fx-text-fill: #555;");

        VBox inhalt = new VBox(8, hinweis, grid);
        inhalt.setPadding(new Insets(8));
        dialog.getDialogPane().setContent(inhalt);

        // Buttons
        ButtonType btnSpeichern = new ButtonType("Speichern",
                ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAbbrechen = new ButtonType("Abbrechen",
                ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes()
                .addAll(btnSpeichern, btnAbbrechen);

        // Ergebnis-Container
        final List<Zeitslot>[] ergebnis = new List[]{null};

        Button speichernBtn = (Button) dialog.getDialogPane()
                .lookupButton(btnSpeichern);

        speichernBtn.addEventFilter(
                javafx.event.ActionEvent.ACTION, e -> {
                    e.consume();

                    ergebnis[0] = alleZeitslots.stream()
                            .filter(z -> {
                                String key = z.getWochentag().name() + "_" +
                                        z.getStundenNummer();
                                CheckBox cb = checkboxen.get(key);
                                return cb != null && cb.isSelected();
                            })
                            .toList();

                    dialog.close();
                }
        );

        dialog.showAndWait();
        return Optional.ofNullable(ergebnis[0]);
    }
}