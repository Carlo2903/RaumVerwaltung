package de.fhswf.raumverwaltung.db.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "schuljahr")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schuljahr {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String bezeichnung;

	@Column(name = "start_datum")
	private LocalDate startdatum;

	@Column(name = "end_datum")
	private LocalDate enddatum;

	private boolean istAktiv;
}