package de.fhswf.raumverwaltung.db.entities;

public enum SchuelerStatus {
    AKTIV,
    ABGEGANGEN;

    @Override
    public String toString() {
        return switch (this) {
            case AKTIV      -> "Aktiv";
            case ABGEGANGEN -> "Abgegangen";
        };
    }
}