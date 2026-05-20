package de.fhswf.raumverwaltung.db.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.Collection;

@Entity
@Table(name = "klasse")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Klasse {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String bezeichnung;
	private int jahrgangsstufe;
	private int schuelerAnzahl;

	// Der Klassenlehrer (Beziehung zur Lehrkraft)
	@ManyToOne
	@JoinColumn(name = "lehrkraft_id")
	private Lehrkraft klassenLehrer;

}