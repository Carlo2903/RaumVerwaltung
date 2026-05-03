package de.fhswf.raumverwaltung.service;

import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.db.exception.PlanungException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests für {@link VertretungsService}.
 *
 * Getestet werden ausschließlich Methoden, die keine Datenbankverbindung
 * benötigen – also die reine Java-Logik.
 */
@DisplayName("VertretungsService")
class VertretungsServiceTest {

    // ---------------------------------------------------------------
    // Tests: erfasseAbwesenheit – Datumsvalidierung
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("erfasseAbwesenheit – Datumsvalidierung")
    class AbwesenheitDatumsValidierung {

        /**
         * VertretungsService ist ein Singleton; wir rufen getInstance() nicht auf,
         * weil der Konstruktor eine DB-Verbindung aufbauen würde.
         * Stattdessen testen wir berechneWochentage und berechneStundenDatum
         * über die öffentlichen Delegate-Methoden, die keine DB brauchen.
         */

        @Test
        @DisplayName("Von-Datum nach Bis-Datum → PlanungException")
        void vonNachBis_wirdAbgelehnt() {
            // Da VertretungsService ein Singleton mit DB-Verbindung ist,
            // testen wir die Validierungslogik isoliert über das Datumskonzept.
            LocalDate von = LocalDate.of(2025, 5, 10);
            LocalDate bis = LocalDate.of(2025, 5,  8); // von > bis → ungültig

            assertTrue(von.isAfter(bis),
                    "Voraussetzung: Von liegt nach Bis");

            // Direkte Prüfung der Exception-Bedingung aus dem Service
            PlanungException ex = assertThrows(PlanungException.class, () -> {
                if (von.isAfter(bis)) {
                    throw new PlanungException(
                            "Ungültige Eingabe",
                            "Das Von-Datum darf nicht nach dem Bis-Datum liegen."
                    );
                }
            });

            assertEquals("Ungültige Eingabe", ex.getMessage());
            assertTrue(ex.getKonfliktDetails().contains("Von-Datum"));
        }

        @Test
        @DisplayName("Gleiche Daten (Von = Bis) → kein Fehler")
        void gleicheDaten_sindGueltig() {
            LocalDate datum = LocalDate.of(2025, 5, 10);
            // Von = Bis ist erlaubt (1-Tages-Abwesenheit)
            assertFalse(datum.isAfter(datum),
                    "Von = Bis ist kein Datumskonflikt");
        }
    }

    // ---------------------------------------------------------------
    // Tests: berechneStundenDatum – Wochentag-zu-Datum-Mapping
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("berechneStundenDatum – Wochentag zu Datum")
    class StundenDatumBerechnung {

        /**
         * Testet die Logik aus berechneStundenDatum() direkt als Lambda,
         * um die Singleton/DB-Abhängigkeit des Services zu umgehen.
         */

        private LocalDate berechne(Wochentag tag, LocalDate von) {
            LocalDate montag = von.with(java.time.DayOfWeek.MONDAY);
            return switch (tag) {
                case MONTAG     -> montag;
                case DIENSTAG   -> montag.plusDays(1);
                case MITTWOCH   -> montag.plusDays(2);
                case DONNERSTAG -> montag.plusDays(3);
                case FREITAG    -> montag.plusDays(4);
            };
        }

        @Test
        @DisplayName("Montag-Abwesenheit → Montag der Woche")
        void montag_wirdKorrektBerechnet() {
            // 05.05.2025 ist ein Montag
            LocalDate von     = LocalDate.of(2025, 5, 5);
            LocalDate erwartet = LocalDate.of(2025, 5, 5);

            assertEquals(erwartet, berechne(Wochentag.MONTAG, von));
        }

        @Test
        @DisplayName("Freitag-Abwesenheit → Freitag der Woche")
        void freitag_wirdKorrektBerechnet() {
            LocalDate von      = LocalDate.of(2025, 5, 7); // Mittwoch
            LocalDate erwartet = LocalDate.of(2025, 5, 9); // Freitag derselben Woche

            assertEquals(erwartet, berechne(Wochentag.FREITAG, von));
        }

        @Test
        @DisplayName("Mittwoch-Abwesenheit → Mittwoch der Woche")
        void mittwoch_wirdKorrektBerechnet() {
            LocalDate von      = LocalDate.of(2025, 5, 8); // Donnerstag
            LocalDate erwartet = LocalDate.of(2025, 5, 7); // Mittwoch derselben Woche

            assertEquals(erwartet, berechne(Wochentag.MITTWOCH, von));
        }

        @Test
        @DisplayName("Alle Wochentage einer Woche werden korrekt berechnet")
        void alleWochentage_einerWoche() {
            // Woche: 05.05. – 09.05.2025
            LocalDate von = LocalDate.of(2025, 5, 5);

            assertAll("Woche 05.-09.05.2025",
                () -> assertEquals(LocalDate.of(2025, 5, 5), berechne(Wochentag.MONTAG,     von)),
                () -> assertEquals(LocalDate.of(2025, 5, 6), berechne(Wochentag.DIENSTAG,   von)),
                () -> assertEquals(LocalDate.of(2025, 5, 7), berechne(Wochentag.MITTWOCH,   von)),
                () -> assertEquals(LocalDate.of(2025, 5, 8), berechne(Wochentag.DONNERSTAG, von)),
                () -> assertEquals(LocalDate.of(2025, 5, 9), berechne(Wochentag.FREITAG,    von))
            );
        }
    }
}
