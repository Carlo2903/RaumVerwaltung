package de.fhswf.raumverwaltung.ui.tabpane.lehrkraft;

import de.fhswf.raumverwaltung.db.entities.Lehrkraft;
import javafx.beans.property.*;
import lombok.Getter;

public class LehrkraftTableEntity {

    @Getter
    private final Lehrkraft lehrkraft;

    private final StringProperty  name         = new SimpleStringProperty();
    private final StringProperty  kuerzel      = new SimpleStringProperty();
    private final IntegerProperty sollStunden  = new SimpleIntegerProperty();

    public LehrkraftTableEntity(Lehrkraft lehrkraft) {
        this.lehrkraft = lehrkraft;
        this.name.set(lehrkraft.getName());
        this.kuerzel.set(lehrkraft.getKuerzel());
        this.sollStunden.set(lehrkraft.getSollStunden());
    }

    public StringProperty  nameProperty()        { return name; }
    public StringProperty  kuerzelProperty()     { return kuerzel; }
    public IntegerProperty sollStundenProperty() { return sollStunden; }
}