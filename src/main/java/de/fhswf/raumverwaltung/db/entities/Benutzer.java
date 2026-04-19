package de.fhswf.raumverwaltung.db.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "benutzer")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Benutzer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String benutzername;

    @Column(nullable = false)
    private String passwortHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Benutzerrolle rolle;

    private boolean aktiv = true;
}