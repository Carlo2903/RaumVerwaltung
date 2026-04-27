package de.fhswf.raumverwaltung.ui.tabpane.stundenplan;

import de.fhswf.raumverwaltung.db.entities.*;
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

}