package de.fhswf.raumverwaltung.ui.tabpane.erziehungsberechtigter;

import de.fhswf.raumverwaltung.db.entities.Erziehungsberechtigter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

public class ErziehungsberechtigterTableEntity {

    @Getter
    private final Erziehungsberechtigter erziehungsberechtigter;

    private final StringProperty nachname    = new SimpleStringProperty();
    private final StringProperty vorname     = new SimpleStringProperty();
    private final StringProperty beziehung   = new SimpleStringProperty();
    private final StringProperty schueler    = new SimpleStringProperty();
    private final StringProperty telefon     = new SimpleStringProperty();
    private final StringProperty email       = new SimpleStringProperty();

    public ErziehungsberechtigterTableEntity(Erziehungsberechtigter e) {
        this.erziehungsberechtigter = e;
        this.nachname.set(e.getNachname());
        this.vorname.set(e.getVorname());
        this.beziehung.set(e.getBeziehung() != null ? e.getBeziehung().name() : "–");
        this.schueler.set(
                e.getSchueler() != null
                        ? e.getSchueler().getNachname() + ", " + e.getSchueler().getVorname()
                        : "–"
        );
        this.telefon.set(e.getTelefon() != null ? e.getTelefon() : "–");
        this.email.set(e.getEmail() != null ? e.getEmail() : "–");
    }

    public StringProperty nachnameProperty()  { return nachname; }
    public StringProperty vornameProperty()   { return vorname; }
    public StringProperty beziehungProperty() { return beziehung; }
    public StringProperty schuelerProperty()  { return schueler; }
    public StringProperty telefonProperty()   { return telefon; }
    public StringProperty emailProperty()     { return email; }
}
