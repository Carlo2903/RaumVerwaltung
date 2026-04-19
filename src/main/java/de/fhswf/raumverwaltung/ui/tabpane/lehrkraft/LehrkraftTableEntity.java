package de.fhswf.raumverwaltung.ui.tabpane.lehrkraft;

import de.fhswf.raumverwaltung.db.entities.Fach;
import de.fhswf.raumverwaltung.db.entities.Lehrkraft;
import javafx.beans.property.*;
import lombok.Getter;

import java.util.stream.Collectors;

public class LehrkraftTableEntity {

    @Getter
    private final Lehrkraft lehrkraft;

    private final StringProperty  name        = new SimpleStringProperty();
    private final StringProperty  kuerzel     = new SimpleStringProperty();
    private final StringProperty  faecher     = new SimpleStringProperty();
    private final IntegerProperty sollStunden = new SimpleIntegerProperty();

    public LehrkraftTableEntity(Lehrkraft lehrkraft) {
        this.lehrkraft = lehrkraft;
        this.name.set(lehrkraft.getName());
        this.kuerzel.set(lehrkraft.getKuerzel());
        this.sollStunden.set(lehrkraft.getSollStunden());

        // Fächer als kommaseparierten String darstellen (wie in eurer Skizze)
        String faecherStr = lehrkraft.getFaecher().stream()
                .map(Fach::getBezeichnung)
                .collect(Collectors.joining(", "));
        this.faecher.set(faecherStr.isEmpty() ? "–" : faecherStr);
    }

    public StringProperty  nameProperty()        { return name; }
    public StringProperty  kuerzelProperty()     { return kuerzel; }
    public StringProperty  faecherProperty()     { return faecher; }
    public IntegerProperty sollStundenProperty() { return sollStunden; }
}