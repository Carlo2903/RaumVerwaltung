package de.fhswf.raumverwaltung.db.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "vertretung")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vertretung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate datum;

    private String bemerkung;

    @Enumerated(EnumType.STRING)
    private VertretungsGrund grund;

    // Die Stunde die vertreten wird
    @ManyToOne
    @JoinColumn(name = "stunde_id", nullable = false)
    private Stunde stunde;

    // Die Lehrkraft die die Vertretung übernimmt
    @ManyToOne
    @JoinColumn(name = "vertretungslehrer_id", nullable = false)
    private Lehrkraft vertretungsLehrer;
}