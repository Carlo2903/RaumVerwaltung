package de.fhswf.raumverwaltung.db.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lehrer_benutzer")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class LehrerBenutzer extends Benutzer {

    @OneToOne
    @JoinColumn(name = "lehrkraft_id", nullable = false)
    private Lehrkraft lehrkraft;

    public LehrerBenutzer(String benutzername, String passwortHash, Lehrkraft lehrkraft) {
        super(null, benutzername, passwortHash, Benutzerrolle.LEHRER, true);
        this.lehrkraft = lehrkraft;
    }
}