package de.fhswf.raumverwaltung.db.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "erziehungsberechtigter")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Erziehungsberechtigter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vorname;

    @Column(nullable = false)
    private String nachname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Beziehung beziehung;

    private String adresse;
    private String telefon;
    private String email;

    // Zugehöriger Schüler
    @ManyToOne
    @JoinColumn(name = "schueler_id", nullable = false)
    @ToString.Exclude
    private Schueler schueler;
}
