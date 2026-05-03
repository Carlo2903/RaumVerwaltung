package de.fhswf.raumverwaltung.ui.tabpane.stundenplan;

import de.fhswf.raumverwaltung.db.entities.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests für die Grid-Logik des Stundenplan-ViewModels.
 *
 * Da das ViewModel intern das Singleton-Model und eine DB-Verbindung verwendet,
 * testen wir hier die Grid-Datenstruktur-Logik isoliert –
 * d. h. das korrekte Befüllen und Auslesen des
 * {@code Map<Wochentag, Map<Integer, Stunde>>}-Grids.
 */
@DisplayName("Stundenplan-Grid-Logik")
class StundenplanGridLogikTest {

    // ---------------------------------------------------------------
    // Hilfsmethoden
    // ---------------------------------------------------------------

    private Zeitslot zeitslot(Wochentag tag, int nr) {
        return new Zeitslot(null, nr, tag,
                LocalTime.of(7 + nr, 0),
                LocalTime.of(7 + nr, 45));
    }

    private Stunde stundeErstellen(Wochentag tag, int nr, String fachName) {
        Stunde s = new Stunde();
        s.setZeitslot(zeitslot(tag, nr));
        Fach fach = Fach.builder()
                .id((long) nr)
                .bezeichnung(fachName)
                .kuerzel(fachName.substring(0, 2).toUpperCase())
                .wochenstundenProKlasse(4)
                .build();
        s.setFach(fach);
        return s;
    }

    /** Baut ein Grid wie das Model es liefern würde. */
    private Map<Wochentag, Map<Integer, Stunde>> bauGrid(List<Stunde> stunden) {
        Map<Wochentag, Map<Integer, Stunde>> grid = new HashMap<>();
        for (Wochentag tag : Wochentag.values()) {
            grid.put(tag, new HashMap<>());
        }
        stunden.forEach(s -> {
            Wochentag tag = s.getZeitslot().getWochentag();
            int nr        = s.getZeitslot().getStundenNummer();
            grid.get(tag).put(nr, s);
        });
        return grid;
    }

    // ---------------------------------------------------------------
    // Tests: Grid-Aufbau
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("Grid-Aufbau")
    class GridAufbau {

        @Test
        @DisplayName("Leeres Grid enthält alle 5 Wochentage")
        void leeresGrid_enthaeltAlleWochentage() {
            Map<Wochentag, Map<Integer, Stunde>> grid = bauGrid(List.of());

            assertEquals(5, grid.size());
            for (Wochentag tag : Wochentag.values()) {
                assertTrue(grid.containsKey(tag),
                        "Wochentag " + tag + " fehlt im Grid");
            }
        }

        @Test
        @DisplayName("Stunde wird an der richtigen Position im Grid abgelegt")
        void stunde_wirdAnRichtigerPositionAbgelegt() {
            Stunde mathe = stundeErstellen(Wochentag.MONTAG, 2, "Mathematik");

            Map<Wochentag, Map<Integer, Stunde>> grid = bauGrid(List.of(mathe));

            Stunde gefunden = grid.get(Wochentag.MONTAG).get(2);
            assertNotNull(gefunden, "Stunde muss an Position MO/2 stehen");
            assertEquals("Mathematik", gefunden.getFach().getBezeichnung());
        }

        @Test
        @DisplayName("Mehrere Stunden an verschiedenen Positionen")
        void mehrereStunden_anVerschiedenenPositionen() {
            Stunde mathe   = stundeErstellen(Wochentag.MONTAG,    1, "Mathematik");
            Stunde deutsch = stundeErstellen(Wochentag.DIENSTAG,  3, "Deutsch");
            Stunde sport   = stundeErstellen(Wochentag.FREITAG,   5, "Sport");

            Map<Wochentag, Map<Integer, Stunde>> grid =
                    bauGrid(List.of(mathe, deutsch, sport));

            assertAll(
                () -> assertEquals("Mathematik",
                        grid.get(Wochentag.MONTAG).get(1).getFach().getBezeichnung()),
                () -> assertEquals("Deutsch",
                        grid.get(Wochentag.DIENSTAG).get(3).getFach().getBezeichnung()),
                () -> assertEquals("Sport",
                        grid.get(Wochentag.FREITAG).get(5).getFach().getBezeichnung())
            );
        }

        @Test
        @DisplayName("Leere Zeitslots liefern null")
        void leereZeitslots_liefernNull() {
            Stunde mathe = stundeErstellen(Wochentag.MONTAG, 1, "Mathematik");
            Map<Wochentag, Map<Integer, Stunde>> grid = bauGrid(List.of(mathe));

            // MO/1 ist belegt – MO/2 muss leer sein
            assertNull(grid.get(Wochentag.MONTAG).get(2),
                    "MO/2 darf nicht belegt sein");
            assertNull(grid.get(Wochentag.DIENSTAG).get(1),
                    "DI/1 darf nicht belegt sein");
        }
    }

    // ---------------------------------------------------------------
    // Tests: Stundenzähler-Logik
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("Stundenzähler (Klasse × Fach)")
    class StundenZaehler {

        @Test
        @DisplayName("Zähler erhöht sich korrekt pro Klasse-Fach-Kombination")
        void zaehler_erhoehtSichProKombination() {
            Klasse klasse5a = Klasse.builder().id(1L).bezeichnung("5a").build();
            Fach   mathe    = Fach.builder().id(1L).bezeichnung("Mathematik").wochenstundenProKlasse(4).build();

            Map<String, Integer> zaehler = new HashMap<>();

            // Simuliert bauGrid()-Logik für den Zähler
            String key = klasse5a.getId() + "_" + mathe.getId();
            zaehler.merge(key, 1, Integer::sum);
            zaehler.merge(key, 1, Integer::sum);
            zaehler.merge(key, 1, Integer::sum);

            assertEquals(3, zaehler.getOrDefault(key, 0),
                    "Nach 3 Stunden muss der Zähler 3 sein");
        }

        @Test
        @DisplayName("Verschiedene Klassen haben getrennte Zähler")
        void verschiedeneKlassen_habenGetrenntZaehler() {
            Klasse k5a = Klasse.builder().id(1L).bezeichnung("5a").build();
            Klasse k5b = Klasse.builder().id(2L).bezeichnung("5b").build();
            Fach   mat = Fach.builder().id(1L).bezeichnung("Mathematik").wochenstundenProKlasse(4).build();

            Map<String, Integer> zaehler = new HashMap<>();
            String key5a = k5a.getId() + "_" + mat.getId();
            String key5b = k5b.getId() + "_" + mat.getId();

            zaehler.merge(key5a, 1, Integer::sum);
            zaehler.merge(key5a, 1, Integer::sum);
            zaehler.merge(key5b, 1, Integer::sum);

            assertAll(
                () -> assertEquals(2, zaehler.getOrDefault(key5a, 0), "5a hat 2 Stunden"),
                () -> assertEquals(1, zaehler.getOrDefault(key5b, 0), "5b hat 1 Stunde")
            );
        }
    }
}
