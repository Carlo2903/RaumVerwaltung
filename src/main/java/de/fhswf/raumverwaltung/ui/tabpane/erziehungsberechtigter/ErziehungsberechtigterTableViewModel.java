package de.fhswf.raumverwaltung.ui.tabpane.erziehungsberechtigter;

import de.fhswf.raumverwaltung.db.entities.Beziehung;
import de.fhswf.raumverwaltung.db.entities.Erziehungsberechtigter;
import de.fhswf.raumverwaltung.db.entities.Schueler;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

public class ErziehungsberechtigterTableViewModel implements Observer {

    private final ErziehungsberechtigterTableModel model;

    @Getter
    private final javafx.beans.property.ObjectProperty<ObservableList<ErziehungsberechtigterTableEntity>>
            erziehungsberechtigteProperty =
            new javafx.beans.property.SimpleObjectProperty<>();

    @Getter
    private final ObservableList<Schueler> schueler =
            FXCollections.observableArrayList();

    @Getter
    private final StringProperty fehlerProperty = new SimpleStringProperty();

    @Getter
    private Erziehungsberechtigter aktuellerDatensatz = null;

    public ErziehungsberechtigterTableViewModel() {
        this.model = ErziehungsberechtigterTableModel.getInstance();
        this.model.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        List<ErziehungsberechtigterTableEntity> tmp = model.getErziehungsberechtigte()
                .stream()
                .map(ErziehungsberechtigterTableEntity::new)
                .collect(Collectors.toList());
        erziehungsberechtigteProperty.set(FXCollections.observableList(tmp));
        schueler.setAll(model.getAlleSchueler());
    }

    public void refresh() { model.loadAll(); }

    public void speichern(String vorname, String nachname, Beziehung beziehung,
                          Schueler schuelerEntity, String adresse,
                          String telefon, String email) {
        fehlerProperty.set(null);

        if (vorname.isBlank()) {
            fehlerProperty.set("Vorname darf nicht leer sein.");
            return;
        }
        if (nachname.isBlank()) {
            fehlerProperty.set("Nachname darf nicht leer sein.");
            return;
        }
        if (beziehung == null) {
            fehlerProperty.set("Bitte eine Beziehung auswählen.");
            return;
        }
        if (schuelerEntity == null) {
            fehlerProperty.set("Bitte einen Schüler auswählen.");
            return;
        }

        if (aktuellerDatensatz == null) {
            Erziehungsberechtigter neu = Erziehungsberechtigter.builder()
                    .vorname(vorname)
                    .nachname(nachname)
                    .beziehung(beziehung)
                    .schueler(schuelerEntity)
                    .adresse(adresse.isBlank() ? null : adresse)
                    .telefon(telefon.isBlank() ? null : telefon)
                    .email(email.isBlank() ? null : email)
                    .build();
            model.speichern(neu);
        } else {
            aktuellerDatensatz.setVorname(vorname);
            aktuellerDatensatz.setNachname(nachname);
            aktuellerDatensatz.setBeziehung(beziehung);
            aktuellerDatensatz.setSchueler(schuelerEntity);
            aktuellerDatensatz.setAdresse(adresse.isBlank() ? null : adresse);
            aktuellerDatensatz.setTelefon(telefon.isBlank() ? null : telefon);
            aktuellerDatensatz.setEmail(email.isBlank() ? null : email);
            model.speichern(aktuellerDatensatz);
        }

        fehlerProperty.set(null);
        aktuellerDatensatz = null;
    }

    public void loeschen() {
        fehlerProperty.set(null);

        if (aktuellerDatensatz == null) {
            fehlerProperty.set("Bitte einen Erziehungsberechtigten auswählen.");
            return;
        }

        try {
            model.loeschen(aktuellerDatensatz);
            aktuellerDatensatz = null;
        } catch (jakarta.persistence.PersistenceException e) {
            fehlerProperty.set("Erziehungsberechtigter kann nicht gelöscht werden.");
        }
    }

    public void datensatzAuswaehlen(Erziehungsberechtigter e) { this.aktuellerDatensatz = e; }
    public void datensatzAbwaehlen()                          { this.aktuellerDatensatz = null; }
}
