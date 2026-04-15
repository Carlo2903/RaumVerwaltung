package de.fhswf.raumverwaltung.db.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lehrkraft")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lehrkraft {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String kuerzel;
	private int sollStunden;

	@Column(columnDefinition = "TEXT")
	private String sperrzeiten; // Als JSON oder Text-String
}