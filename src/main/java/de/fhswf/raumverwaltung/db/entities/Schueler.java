package de.fhswf.raumverwaltung.db.entities;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "schueler")
public class Schueler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private LocalDate geburtsdatum;

    private String geschlecht;

    @ManyToOne
    @JoinColumn(name = "klasse_id", nullable = false)
    private Klasse klasse;

    @Column(nullable = false)
    private String status = "aktiv";

    @ManyToMany(mappedBy = "schuelerListe")
    private List<Erziehungsberechtigte> eltern;

    public Schueler() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getGeburtsdatum() { return geburtsdatum; }
    public void setGeburtsdatum(LocalDate geburtsdatum) { this.geburtsdatum = geburtsdatum; }

    public String getGeschlecht() { return geschlecht; }
    public void setGeschlecht(String geschlecht) { this.geschlecht = geschlecht; }

    public Klasse getKlasse() { return klasse; }
    public void setKlasse(Klasse klasse) { this.klasse = klasse; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<Erziehungsberechtigte> getEltern() { return eltern; }
    public void setEltern(List<Erziehungsberechtigte> eltern) { this.eltern = eltern; }
}