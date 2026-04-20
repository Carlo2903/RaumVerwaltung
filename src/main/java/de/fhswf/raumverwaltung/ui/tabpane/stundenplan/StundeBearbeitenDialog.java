package de.fhswf.raumverwaltung.ui.tabpane.stundenplan;

import de.fhswf.raumverwaltung.db.dao.FachDao;
import de.fhswf.raumverwaltung.db.dao.LehrkraftDao;
import de.fhswf.raumverwaltung.db.dao.RaumDao;
import de.fhswf.raumverwaltung.db.dao.ZeitslotDao;
import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.db.exception.PlanungException;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Optional;

public class StundeBearbeitenDialog {

    // Statische Methode – kein Objekt nötig, einfach aufrufen
    public static void zeige(StundenplanViewModel viewModel,
                             Stunde vorhandeneStunde,
                             Wochentag tag,
                             int stundenNummer) {

        // DAOs für Dropdown-Daten
        FachDao      fachDao      = new FachDao();
        LehrkraftDao lehrkraftDao = new LehrkraftDao();
        RaumDao      raumDao      = new RaumDao();
        ZeitslotDao  zeitslotDao  = new ZeitslotDao();

        // Dialog aufbauen
        Dialog<Stunde> dialog = new Dialog<>();
        dialog.setTitle(vorhandeneStunde == null ? "Stunde anlegen" : "Stunde bearbeiten");

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(8);
        form.setPadding(new Insets(16));

        // Dropdowns
        ComboBox<Fach>      fachBox     = new ComboBox<>();
        ComboBox<Lehrkraft> lehrerBox   = new ComboBox<>();
        ComboBox<Raum>      raumBox     = new ComboBox<>();
        ComboBox<Klasse>    klasseBox   = new ComboBox<>();

        fachBox.getItems().addAll(fachDao.findAll());
        lehrerBox.getItems().addAll(lehrkraftDao.findAll());
        raumBox.getItems().addAll(raumDao.findAll());
        klasseBox.getItems().addAll(viewModel.getKlassenProperty().get());

        // Vorhandene Stunde vorbelegen
        if (vorhandeneStunde != null) {
            fachBox.setValue(vorhandeneStunde.getFach());
            lehrerBox.setValue(vorhandeneStunde.getLehrkraft());
            raumBox.setValue(vorhandeneStunde.getRaum());
            klasseBox.setValue(vorhandeneStunde.getKlasse());
        }

        // Konflikt-Hinweis Label
        Label konfliktLabel = new Label();
        konfliktLabel.setStyle("-fx-text-fill: #cf222e;");
        konfliktLabel.setVisible(false);
        konfliktLabel.setManaged(false);

        form.add(new Label("Fach:"),      0, 0); form.add(fachBox,   1, 0);
        form.add(new Label("Lehrkraft:"), 0, 1); form.add(lehrerBox, 1, 1);
        form.add(new Label("Raum:"),      0, 2); form.add(raumBox,   1, 2);
        form.add(new Label("Klasse:"),    0, 3); form.add(klasseBox, 1, 3);
        form.add(konfliktLabel,           0, 4, 2, 1);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(
                ButtonType.OK, ButtonType.CANCEL
        );

        // Löschen-Button nur bei bestehender Stunde
        if (vorhandeneStunde != null) {
            dialog.getDialogPane().getButtonTypes().add(ButtonType.APPLY);
            Button btnLoeschen = (Button) dialog.getDialogPane()
                    .lookupButton(ButtonType.APPLY);
            btnLoeschen.setText("Löschen");
            btnLoeschen.setStyle("-fx-background-color: #cf222e; -fx-text-fill: white;");
            btnLoeschen.setOnAction(e -> {
                viewModel.stundeLoeschen(vorhandeneStunde);
                dialog.close();
            });
        }

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                // Zeitslot für diesen Tag + Stundennummer suchen
                List<Zeitslot> slots = zeitslotDao.findeNachWochentag(tag);
                Optional<Zeitslot> slot = slots.stream()
                        .filter(z -> z.getStundenNummer() == stundenNummer)
                        .findFirst();

                if (slot.isEmpty()) {
                    zeigeKonflikt(konfliktLabel,
                            "Kein Zeitslot für diesen Tag gefunden.");
                    return null;
                }

                Stunde stunde = vorhandeneStunde != null
                        ? vorhandeneStunde
                        : new Stunde();

                stunde.setFach(fachBox.getValue());
                stunde.setLehrkraft(lehrerBox.getValue());
                stunde.setRaum(raumBox.getValue());
                stunde.setKlasse(klasseBox.getValue());
                stunde.setZeitslot(slot.get());
                stunde.setStundenplan(viewModel.getAktuellerPlan());

                try {
                    viewModel.stundeSetzen(stunde);
                } catch (PlanungException e) {
                    zeigeKonflikt(konfliktLabel, e.getKonfliktDetails());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private static void zeigeKonflikt(Label label, String text) {
        label.setText("⚠ " + text);
        label.setVisible(true);
        label.setManaged(true);
    }
}