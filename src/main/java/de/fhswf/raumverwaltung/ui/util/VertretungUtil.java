package de.fhswf.raumverwaltung.ui.util;

import de.fhswf.raumverwaltung.db.entities.Stunde;
import de.fhswf.raumverwaltung.db.entities.Vertretung;
import java.util.Map;

public class VertretungUtil {

    public static String getVertretungslehrerName(
            Stunde stunde, Map<Long, Vertretung> vertretungenProStunde) {
        if (stunde == null || stunde.getId() == null) return "–";
        Vertretung v = vertretungenProStunde.get(stunde.getId());
        return v != null ? v.getVertretungsLehrer().getName() : "–";
    }
}