package de.fhswf.raumverwaltung.db.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "schueler")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schueler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vorname;

    @Column(nullable = false)
    private String nachname;

    private LocalDate geburtsdatum;

    @Enumerated(EnumType.STRING)
    private SchuelerStatus status;

    @ManyToOne
    @JoinColumn(name = "klasse_id", nullable = false)
    private Klasse klasse;
}