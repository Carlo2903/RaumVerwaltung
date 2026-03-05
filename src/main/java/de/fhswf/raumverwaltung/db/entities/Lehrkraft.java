package de.fhswf.raumverwaltung.db.entities;
import jakarta.persistence.*;

@Entity
@Table(name = "lehrkraefte")
public class Lehrkraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false, length = 10)
    private String kuerzel;

    @Column(name = "soll_stunden", nullable = false)
    private int sollStunden;

    @Column(columnDefinition = "TEXT")
    private String sperrzeiten;

    // Leerer Konstruktor für JPA
    public Lehrkraft() {}

    // Getter und Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getKuerzel() { return kuerzel; }
    public void setKuerzel(String kuerzel) { this.kuerzel = kuerzel; }

    public int getSollStunden() { return sollStunden; }
    public void setSollStunden(int sollStunden) { this.sollStunden = sollStunden; }

    public String getSperrzeiten() { return sperrzeiten; }
    public void setSperrzeiten(String sperrzeiten) { this.sperrzeiten = sperrzeiten; }
}