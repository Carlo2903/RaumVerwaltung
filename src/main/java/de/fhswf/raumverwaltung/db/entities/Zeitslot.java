package de.fhswf.raumverwaltung.db.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Table(name = "zeitslot")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Zeitslot {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private int stundenNummer; // z.B. 1, 2, 3...

	@Enumerated(EnumType.STRING)
	private Wochentag wochentag;

	private LocalTime startzeit;
	private LocalTime endzeit;
}