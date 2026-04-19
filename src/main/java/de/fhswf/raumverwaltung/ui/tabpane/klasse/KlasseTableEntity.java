package de.fhswf.raumverwaltung.ui.tabpane.klasse;

import de.fhswf.raumverwaltung.db.entities.Klasse;
import javafx.beans.property.*;
import lombok.Getter;

public class KlasseTableEntity {

    @Getter
    private final Klasse klasse;

    private final StringProperty  bezeichnung    = new SimpleStringProperty();
    private final IntegerProperty jahrgangsstufe = new SimpleIntegerProperty();
    private final StringProperty  klassenLehrer  = new SimpleStringProperty();

    public KlasseTableEntity(Klasse klasse) {
        this.klasse = klasse;
        this.bezeichnung.set(klasse.getBezeichnung());
        this.jahrgangsstufe.set(klasse.getJahrgangsstufe());

        // Klassenlehrer kann null sein – defensiv abfangen
        String lehrername = klasse.getKlassenLehrer() != null
                ? klasse.getKlassenLehrer().getName()
                : "–";
        this.klassenLehrer.set(lehrername);
    }

    public StringProperty  bezeichnungProperty()    { return bezeichnung; }
    public IntegerProperty jahrgangsstufeProperty() { return jahrgangsstufe; }
    public StringProperty  klassenlehrerProperty()  { return klassenLehrer; }
}