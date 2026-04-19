package de.fhswf.raumverwaltung.db.entities;

public enum RaumTyp {
	STANDARD,
	FACHRAUM_CHEMIE,
	MUSIK,
	SPORTHALLE,
	INFORMATIK;

	@Override
	public String toString() {
		return switch (this) {
			case STANDARD        -> "Standard";
			case FACHRAUM_CHEMIE -> "Fachraum Chemie";
			case MUSIK           -> "Musik";
			case SPORTHALLE      -> "Sporthalle";
			case INFORMATIK      -> "Informatik";
		};
	}
}