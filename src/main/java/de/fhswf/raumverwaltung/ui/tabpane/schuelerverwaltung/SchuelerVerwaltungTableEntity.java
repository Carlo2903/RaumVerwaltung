package de.fhswf.raumverwaltung.ui.tabpane.schuelerverwaltung;

import de.fhswf.raumverwaltung.db.entities.Schueler;
import javafx.beans.property.*;
import lombok.Getter;

public class SchuelerVerwaltungTableEntity {

    @Getter
    private final Schueler schueler;

    private final StringProperty nachname     = new SimpleStringProperty();
    private final StringProperty vorname      = new SimpleStringProperty();
    private final StringProperty geburtsdatum = new SimpleStringProperty();
    private final StringProperty klasse       = new SimpleStringProperty();
    private final StringProperty status       = new SimpleStringProperty();

    public SchuelerVerwaltungTableEntity(Schueler schueler) {
        this.schueler = schueler;
        this.nachname.set(schueler.getNachname());
        this.vorname.set(schueler.getVorname());
        this.geburtsdatum.set(
                schueler.getGeburtsdatum() != null
                        ? schueler.getGeburtsdatum().toString() : "–"
        );
        this.klasse.set(
                schueler.getKlasse() != null
                        ? schueler.getKlasse().getBezeichnung() : "–"
        );
        this.status.set(
                schueler.getStatus() != null
                        ? schueler.getStatus().toString() : "–"
        );
    }

    public StringProperty nachnameProperty()     { return nachname; }
    public StringProperty vornameProperty()      { return vorname; }
    public StringProperty geburtsdatumProperty() { return geburtsdatum; }
    public StringProperty klasseProperty()       { return klasse; }
    public StringProperty statusProperty()       { return status; }
}