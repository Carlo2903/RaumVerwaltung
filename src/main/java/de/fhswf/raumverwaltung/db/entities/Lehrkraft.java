package de.fhswf.raumverwaltung.db.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lehrkraft")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lehrkraft {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String kuerzel;
	private int sollStunden;

	// NEU: Fächer als echte Relation
	@ManyToMany
	@JoinTable(
			name = "lehrkraft_fach",
			joinColumns = @JoinColumn(name = "lehrkraft_id"),
			inverseJoinColumns = @JoinColumn(name = "fach_id")
	)
	@ToString.Exclude
	private List<Fach> faecher = new ArrayList<>();

	// NEU: Sperrzeiten als echte Relation (ersetzt den String)
	@OneToMany(mappedBy = "lehrkraft", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private List<Sperrzeit> sperrzeiten = new ArrayList<>();
}