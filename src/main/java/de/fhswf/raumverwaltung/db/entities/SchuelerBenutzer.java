package de.fhswf.raumverwaltung.db.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "schueler_benutzer")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SchuelerBenutzer extends Benutzer {

    // Klasse direkt referenzieren – Schüler haben noch kein eigenes Entity
    @ManyToOne
    @JoinColumn(name = "klasse_id", nullable = false)
    private Klasse klasse;

    public SchuelerBenutzer(String benutzername, String passwortHash, Klasse klasse) {
        super(null, benutzername, passwortHash, Benutzerrolle.SCHUELER, true);
        this.klasse = klasse;
    }
}