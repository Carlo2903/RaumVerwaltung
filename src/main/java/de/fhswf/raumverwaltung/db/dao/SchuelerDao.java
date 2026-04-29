package de.fhswf.raumverwaltung.db.dao;

import de.fhswf.raumverwaltung.db.entities.Schueler;

public class SchuelerDao extends GenericDao<Schueler> {
    // GenericDao liefert findAll(), persist(), merge(), remove()
    // Schüler werden nirgendwo referenziert – kein wirdVerwendet() nötig
}