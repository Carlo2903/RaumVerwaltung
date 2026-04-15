package de.fhswf.raumverwaltung.db.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stunde")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stunde {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "stundenplan_id")
	private Stundenplan stundenplan;

	@ManyToOne
	@JoinColumn(name = "lehrkraft_id")
	private Lehrkraft lehrkraft;

	@ManyToOne
	@JoinColumn(name = "raum_id")
	private Raum raum;

	@ManyToOne
	@JoinColumn(name = "klasse_id")
	private Klasse klasse;

	@ManyToOne
	@JoinColumn(name = "fach_id")
	private Fach fach;

	@ManyToOne
	@JoinColumn(name = "zeitslot_id")
	private Zeitslot zeitslot;

	private boolean istAusfall;
	private boolean istVertretung;
	private boolean geaendert;
}