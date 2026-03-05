package de.fhswf.raumverwaltung.db.entities;
import jakarta.persistence.*;

@Entity
@Table(name = "klassen")
public class Klasse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String bezeichnung;

    @Column(nullable = false)
    private int jahrgangsstufe;

    @ManyToOne
    @JoinColumn(name = "klassenlehrer_id")
    private Lehrkraft klassenlehrer;

    @Column(name = "schueler_anzahl")
    private Integer schuelerAnzahl;

    public Klasse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBezeichnung() { return bezeichnung; }
    public void setBezeichnung(String bezeichnung) { this.bezeichnung = bezeichnung; }

    public int getJahrgangsstufe() { return jahrgangsstufe; }
    public void setJahrgangsstufe(int jahrgangsstufe) { this.jahrgangsstufe = jahrgangsstufe; }

    public Lehrkraft getKlassenlehrer() { return klassenlehrer; }
    public void setKlassenlehrer(Lehrkraft klassenlehrer) { this.klassenlehrer = klassenlehrer; }

    public Integer getSchuelerAnzahl() { return schuelerAnzahl; }
    public void setSchuelerAnzahl(Integer schuelerAnzahl) { this.schuelerAnzahl = schuelerAnzahl; }
}