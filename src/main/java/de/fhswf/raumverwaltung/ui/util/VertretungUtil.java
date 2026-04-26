package de.fhswf.raumverwaltung.ui.util;

import de.fhswf.raumverwaltung.db.entities.Stunde;
import de.fhswf.raumverwaltung.db.entities.Vertretung;

import java.time.LocalDate;
import java.util.Map;

public class VertretungUtil {

    // Mit Datum – nur wenn Datum übereinstimmt
    public static String getVertretungslehrerName(
            Stunde stunde,
            Map<Long, Vertretung> vertretungenProStunde,
            LocalDate datum) {
        if (stunde == null || stunde.getId() == null) return "–";
        Vertretung v = vertretungenProStunde.get(stunde.getId());
        if (v == null || !v.getDatum().equals(datum)) return "–";
        return v.getVertretungsLehrer().getName();
    }

    // Ohne Datum – zeigt immer den Vertretungslehrer
    public static String getVertretungslehrerName(
            Stunde stunde,
            Map<Long, Vertretung> vertretungenProStunde) {
        if (stunde == null || stunde.getId() == null) return "–";
        Vertretung v = vertretungenProStunde.get(stunde.getId());
        return v != null ? v.getVertretungsLehrer().getName() : "–";
    }
}