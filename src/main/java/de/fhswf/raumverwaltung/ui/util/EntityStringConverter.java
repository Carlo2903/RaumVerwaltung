package de.fhswf.raumverwaltung.ui.util;

import de.fhswf.raumverwaltung.db.entities.*;
import javafx.util.StringConverter;

public class EntityStringConverter {

    public static StringConverter<Lehrkraft> forLehrkraft() {
        return new StringConverter<>() {
            @Override
            public String toString(Lehrkraft lk) {
                return lk == null ? "" : lk.getName() + " (" + lk.getKuerzel() + ")";
            }
            @Override
            public Lehrkraft fromString(String s) { return null; }
        };
    }

    public static StringConverter<Klasse> forKlasse() {
        return new StringConverter<>() {
            @Override
            public String toString(Klasse k) {
                return k == null ? "" : k.getBezeichnung();
            }
            @Override
            public Klasse fromString(String s) { return null; }
        };
    }

    public static StringConverter<Fach> forFach() {
        return new StringConverter<>() {
            @Override
            public String toString(Fach f) {
                return f == null ? "" : f.getBezeichnung();
            }
            @Override
            public Fach fromString(String s) { return null; }
        };
    }

    public static StringConverter<Raum> forRaum() {
        return new StringConverter<>() {
            @Override
            public String toString(Raum r) {
                return r == null ? "" : r.getBezeichnung();
            }
            @Override
            public Raum fromString(String s) { return null; }
        };
    }
}