package de.fhswf.raumverwaltung.db.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "raum")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Raum {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String bezeichnung;

	@Enumerated(EnumType.STRING)
	private RaumTyp raumtyp;

	private int kapazitaet;
}