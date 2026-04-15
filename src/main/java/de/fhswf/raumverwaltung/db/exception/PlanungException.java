package de.fhswf.raumverwaltung.db.exception;

import lombok.Getter;

@Getter
public class PlanungException extends Exception {
    private final String konfliktDetails;
    public PlanungException(String message, String details) {
        super(message);
        this.konfliktDetails = details;
    }
}