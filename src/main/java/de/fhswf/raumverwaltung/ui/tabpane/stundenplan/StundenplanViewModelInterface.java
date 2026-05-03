package de.fhswf.raumverwaltung.ui.tabpane.stundenplan;

import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.db.exception.PlanungException;
import javafx.beans.property.*;
import javafx.collections.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StundenplanViewModelInterface {
    ObjectProperty<Map<Wochentag, Map<Integer, Stunde>>> getGridProperty();
    ObjectProperty<ObservableList<Klasse>> getKlassenProperty();
    ObservableList<Stunde> getStundenListe();
    void laden();
    void filterNachKlasse(Klasse klasse);
    Stundenplan getAktuellerPlan();
    int getStundenZaehler(Klasse klasse, Fach fach);
    String getVertretungslehrerName(Stunde stunde);
    int getLehrkraftStunden(Lehrkraft lehrkraft);

    /**
     * Öffnet den Dialog zum Bearbeiten einer Stunde.
     * Im Read-Only-Modus (z. B. Lehrersicht) ist die Implementierung eine leere Methode.
     *
     * @param stunde       die zu bearbeitende Stunde, oder {@code null} für eine neue Stunde
     * @param tag          der Wochentag der Zelle
     * @param stundenNummer die Stundennummer der Zelle
     */
    void zeigeBearbeitenDialog(Stunde stunde, Wochentag tag, int stundenNummer);
}