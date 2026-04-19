package de.fhswf.raumverwaltung.db.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "abwesenheit")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Abwesenheit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate von;

    @Column(nullable = false)
    private LocalDate bis;

    @Enumerated(EnumType.STRING)
    private VertretungsGrund grund;

    private String bemerkung;

    private boolean aktiv = true;

    @ManyToOne
    @JoinColumn(name = "lehrkraft_id", nullable = false)
    private Lehrkraft lehrkraft;
}