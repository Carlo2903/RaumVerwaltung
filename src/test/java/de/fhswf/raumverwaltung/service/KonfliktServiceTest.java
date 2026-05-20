package de.fhswf.raumverwaltung.service;

import de.fhswf.raumverwaltung.db.dao.SperrzeitDao;
import de.fhswf.raumverwaltung.db.dao.StundeDao;
import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.db.exception.PlanungException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests für {@link KonfliktService}.
 *
 * Da KonfliktService DAOs intern per { ew} erzeugt, testen wir hier
 * nur die Fälle, die keine DB-Abfrage auslösen (Null-Checks, Logik auf
 * bereits geladenen Daten). Für die DB-abhängigen Pfade sind Integrationstests
 * notwendig (siehe KonfliktServiceIntegrationTest).
 */
@DisplayName("KonfliktService")
class KonfliktServiceTest {

    // ---------------------------------------------------------------
    // Hilfsmethoden zum Aufbauen von Test-Objekten
    // ---------------------------------------------------------------

    private Lehrkraft lehrkraftMit(Long id, String name, int sollStunden) {
        return Lehrkraft.builder()
                .id(id)
                .name(name)
                .kuerzel(name.substring(0, 2).toUpperCase())
                .sollStunden(sollStunden)
                .build();
    }

    private Raum raumMit(Long id, String bezeichnung) {
        return Raum.builder()
                .id(id)
                .bezeichnung(bezeichnung)
                .raumtyp(RaumTyp.STANDARD)
                .kapazitaet(30)
                .build();
    }

    private Klasse klasseMit(Long id, String bezeichnung) {
        return Klasse.builder()
                .id(id)
                .bezeichnung(bezeichnung)
                .jahrgangsstufe(Integer.parseInt(bezeichnung.substring(0, 1)))
                .build();
    }

    private Fach fachMit(Long id, String name, int wochenstunden) {
        return Fach.builder()
                .id(id)
                .bezeichnung(name)
                .kuerzel(name.substring(0, 2).toUpperCase())
                .wochenstundenProKlasse(wochenstunden)
                .build();
    }

    private Zeitslot zeitslotMit(Long id, Wochentag tag, int nummer) {
        return new Zeitslot(id, nummer, tag,
                LocalTime.of(8 + nummer - 1, 0),
                LocalTime.of(8 + nummer - 1, 45));
    }

    private Stunde stundeAufbauen(Stundenplan plan, Lehrkraft lk,
                                   Raum raum, Klasse klasse,
                                   Fach fach, Zeitslot slot) {
        Stunde s = new Stunde();
        s.setStundenplan(plan);
        s.setLehrkraft(lk);
        s.setRaum(raum);
        s.setKlasse(klasse);
        s.setFach(fach);
        s.setZeitslot(slot);
        return s;
    }

    // ---------------------------------------------------------------
    // Tests: Null-Checks (Pflichtfelder)
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("Null-Checks – Pflichtfelder")
    class NullChecks {

        private KonfliktService service;
        private Stundenplan plan;
        private Lehrkraft lk;
        private Raum raum;
        private Klasse klasse;
        private Fach fach;
        private Zeitslot slot;

        @BeforeEach
        void setUp() {
            // KonfliktService braucht keine externe Abhängigkeit für Null-Checks
            service = new KonfliktService();
            plan    = new Stundenplan();
            lk      = lehrkraftMit(1L, "Mueller", 20);
            raum    = raumMit(1L, "101");
            klasse  = klasseMit(1L, "5a");
            fach    = fachMit(1L, "Mathematik", 4);
            slot    = zeitslotMit(1L, Wochentag.MONTAG, 1);
        }

        @Test
        @DisplayName("Fehlender Zeitslot → PlanungException")
        void fehlenderZeitslot_wirdAbgelehnt() {
            Stunde stunde = stundeAufbauen(plan, lk, raum, klasse, fach, null);

            PlanungException ex = assertThrows(PlanungException.class,
                    () -> service.validiereStunde(stunde));

            assertEquals("Unvollständige Daten", ex.getMessage());
            assertTrue(ex.getKonfliktDetails().contains("Zeitslot"));
        }

        @Test
        @DisplayName("Fehlende Lehrkraft → PlanungException")
        void fehlendeLehrer_wirdAbgelehnt() {
            Stunde stunde = stundeAufbauen(plan, null, raum, klasse, fach, slot);

            PlanungException ex = assertThrows(PlanungException.class,
                    () -> service.validiereStunde(stunde));

            assertEquals("Unvollständige Daten", ex.getMessage());
            assertTrue(ex.getKonfliktDetails().contains("Lehrkraft"));
        }

        @Test
        @DisplayName("Fehlender Raum → PlanungException")
        void fehlenderRaum_wirdAbgelehnt() {
            Stunde stunde = stundeAufbauen(plan, lk, null, klasse, fach, slot);

            PlanungException ex = assertThrows(PlanungException.class,
                    () -> service.validiereStunde(stunde));

            assertEquals("Unvollständige Daten", ex.getMessage());
            assertTrue(ex.getKonfliktDetails().contains("Raum"));
        }

        @Test
        @DisplayName("Fehlende Klasse → PlanungException")
        void fehlenderKlasse_wirdAbgelehnt() {
            Stunde stunde = stundeAufbauen(plan, lk, raum, null, fach, slot);

            PlanungException ex = assertThrows(PlanungException.class,
                    () -> service.validiereStunde(stunde));

            assertEquals("Unvollständige Daten", ex.getMessage());
            assertTrue(ex.getKonfliktDetails().contains("Klasse"));
        }
    }
}
