package de.fhswf.raumverwaltung.ui.tabpane.vertretung;

import de.fhswf.raumverwaltung.db.entities.Abwesenheit;

public record AbwesenheitUebersicht(
        Abwesenheit abwesenheit,
        int         betroffeneStunden,
        int         zugewieseneVertretungen
) {
    public boolean isVollstaendig() {
        return betroffeneStunden > 0 &&
                zugewieseneVertretungen >= betroffeneStunden;
    }

    public String getStatusText() {
        return zugewieseneVertretungen + "/" + betroffeneStunden + " zugewiesen";
    }
}