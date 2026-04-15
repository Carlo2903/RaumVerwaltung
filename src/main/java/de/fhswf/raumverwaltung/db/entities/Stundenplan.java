package de.fhswf.raumverwaltung.db.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stundenplan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stundenplan {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private java.util.Date gueltigAb;

	@OneToMany(mappedBy = "stundenplan", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude // Verhindert Endlosschleifen beim Loggen
	private List<Stunde> stunden = new ArrayList<>();
}