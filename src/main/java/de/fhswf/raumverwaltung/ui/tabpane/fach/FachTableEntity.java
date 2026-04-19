package de.fhswf.raumverwaltung.ui.tabpane.fach;

import de.fhswf.raumverwaltung.db.entities.Fach;
import javafx.beans.property.*;
import lombok.Getter;

public class FachTableEntity {

    @Getter
    private final Fach fach;

    private final StringProperty  bezeichnung          = new SimpleStringProperty();
    private final StringProperty  kuerzel              = new SimpleStringProperty();
    private final IntegerProperty wochenstundenProKlasse = new SimpleIntegerProperty();

    public FachTableEntity(Fach fach) {
        this.fach = fach;
        this.bezeichnung.set(fach.getBezeichnung());
        this.kuerzel.set(fach.getKuerzel());
        this.wochenstundenProKlasse.set(fach.getWochenstundenProKlasse());
    }

    public StringProperty  bezeichnungProperty()            { return bezeichnung; }
    public StringProperty  kuerzelProperty()                { return kuerzel; }
    public IntegerProperty wochenstundenProKlasseProperty() { return wochenstundenProKlasse; }
}