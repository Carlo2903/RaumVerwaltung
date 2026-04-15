package de.fhswf.raumverwaltung.ui.tabpane.raum;

import de.fhswf.raumverwaltung.db.entities.Raum;
import de.fhswf.raumverwaltung.db.entities.RaumTyp;
import javafx.beans.property.*;

public class RaumTableEntity {
    private final StringProperty bezeichnung;
    private final ObjectProperty<RaumTyp> raumtyp;
    private final IntegerProperty kapazitaet;
    private final Raum raum;

    public RaumTableEntity(Raum raum) {
        this.raum = raum;
        this.bezeichnung = new SimpleStringProperty(raum.getBezeichnung());
        this.raumtyp = new SimpleObjectProperty<>(raum.getRaumtyp());
        this.kapazitaet = new SimpleIntegerProperty(raum.getKapazitaet());
    }

    public StringProperty bezeichnungProperty() { return bezeichnung; }
    public ObjectProperty<RaumTyp> raumtypProperty() { return raumtyp; }
    public IntegerProperty kapazitaetProperty() { return kapazitaet; }
    public Raum getRaum() { return raum; }
}