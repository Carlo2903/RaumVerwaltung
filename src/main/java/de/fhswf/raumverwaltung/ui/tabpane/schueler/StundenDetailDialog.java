package de.fhswf.raumverwaltung.ui.tabpane.schueler;

import de.fhswf.raumverwaltung.db.entities.Stunde;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;

public class StundenDetailDialog {

    public static void zeige(Stunde stunde, LocalDate datum,
                             boolean hatVertretung, String vertretungsName) {

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Stundendetails");

        VBox inhalt = new VBox(12);
        inhalt.setPadding(new Insets(16));
        inhalt.setPrefWidth(300);

        // Fach
        inhalt.getChildren().add(buildZeile("Fach:",
                stunde.getFach() != null
                        ? stunde.getFach().getBezeichnung() : "–"));

        // Zeit
        String zeit = stunde.getZeitslot().getStartzeit() +
                " – " +
                stunde.getZeitslot().getEndzeit();
        inhalt.getChildren().add(buildZeile("Zeit:", zeit));

        // Lehrer
        if (hatVertretung) {
            inhalt.getChildren().add(buildZeile("Lehrkraft:",
                    vertretungsName + " (Vertretung)"));
        } else {
            String lehrer = stunde.getLehrkraft() != null
                    ? stunde.getLehrkraft().getName() +
                    " (" + stunde.getLehrkraft().getKuerzel() + ")"
                    : "–";
            inhalt.getChildren().add(buildZeile("Lehrkraft:", lehrer));
        }

        // Raum
        inhalt.getChildren().add(buildZeile("Raum:",
                stunde.getRaum() != null
                        ? stunde.getRaum().getBezeichnung() : "–"));

        // Klasse
        inhalt.getChildren().add(buildZeile("Klasse:",
                stunde.getKlasse() != null
                        ? stunde.getKlasse().getBezeichnung() : "–"));

        // Status
        String status = stunde.isIstAusfall()  ? "✕ Ausfall" :
                hatVertretung          ? "⚠ Vertretung" :
                        "✓ Regulär";
        String statusFarbe = stunde.isIstAusfall() ? "#cf222e" :
                hatVertretung          ? "#bf8700" :
                        "#1a7f37";

        Label lblStatus = new Label(status);
        lblStatus.setStyle(
                "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + statusFarbe + ";"
        );
        inhalt.getChildren().add(buildZeileCustom("Status:", lblStatus));

        dialog.getDialogPane().setContent(inhalt);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private static HBox buildZeile(String label, String wert) {
        Label lblWert = new Label(wert);
        return buildZeileCustom(label, lblWert);
    }

    private static HBox buildZeileCustom(String label, javafx.scene.Node wert) {
        Label lblLabel = new Label(label);
        lblLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 90;");
        HBox zeile = new HBox(8, lblLabel, wert);
        return zeile;
    }
}