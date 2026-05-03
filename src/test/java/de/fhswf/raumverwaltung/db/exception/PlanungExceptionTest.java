package de.fhswf.raumverwaltung.db.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests für {@link PlanungException}.
 *
 * Prüft, dass die Exception Titel und Details korrekt trennt –
 * wichtig für die Darstellung in der UI (Alert-Dialog).
 */
@DisplayName("PlanungException")
class PlanungExceptionTest {

    @Test
    @DisplayName("Titel wird als Message gesetzt")
    void titel_wirdAlsMessageGesetzt() {
        PlanungException ex = new PlanungException("Stundenplan-Konflikt", "Raum 101 ist belegt.");

        assertEquals("Stundenplan-Konflikt", ex.getMessage());
    }

    @Test
    @DisplayName("Details werden separat gespeichert")
    void details_werdenSeparatGespeichert() {
        String details = "Lehrkraft Müller hat zu diesem Zeitslot bereits Unterricht.";
        PlanungException ex = new PlanungException("Konflikt", details);

        assertEquals(details, ex.getKonfliktDetails());
    }

    @Test
    @DisplayName("Titel und Details sind unabhängig voneinander")
    void titelUndDetails_sindUnabhaengig() {
        PlanungException ex = new PlanungException("Titel", "Details");

        assertAll(
            () -> assertEquals("Titel",   ex.getMessage()),
            () -> assertEquals("Details", ex.getKonfliktDetails())
        );
    }

    @Test
    @DisplayName("PlanungException ist eine checked Exception")
    void planungException_istChecked() {
        // Checked Exception → muss von Exception erben, nicht von RuntimeException
        assertTrue(Exception.class.isAssignableFrom(PlanungException.class));
        assertFalse(RuntimeException.class.isAssignableFrom(PlanungException.class));
    }

    @Test
    @DisplayName("PlanungException kann geworfen und gefangen werden")
    void kannGeworfenUndGefangenWerden() {
        assertThrows(PlanungException.class, () -> {
            throw new PlanungException("Test", "Testdetails");
        });
    }
}
