package de.fhswf.raumverwaltung.db.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admin_benutzer")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AdminBenutzer extends Benutzer {

    public AdminBenutzer(String benutzername, String passwortHash) {
        super(null, benutzername, passwortHash, Benutzerrolle.ADMINISTRATOR, true);
    }
}