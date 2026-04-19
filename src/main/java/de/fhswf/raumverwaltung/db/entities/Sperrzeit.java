package de.fhswf.raumverwaltung.db.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sperrzeit")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sperrzeit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lehrkraft_id", nullable = false)
    private Lehrkraft lehrkraft;

    @ManyToOne
    @JoinColumn(name = "zeitslot_id", nullable = false)
    private Zeitslot zeitslot;
}
