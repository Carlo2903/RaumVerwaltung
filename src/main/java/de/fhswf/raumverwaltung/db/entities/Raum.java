package de.fhswf.raumverwaltung.db.entities;
import jakarta.persistence.*;

@Entity
@Table(name = "raeume")
public class Raum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String bezeichnung;

    @Column(nullable = false)
    private String typ;

    private Integer kapazitaet;

    public Raum() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBezeichnung() { return bezeichnung; }
    public void setBezeichnung(String bezeichnung) { this.bezeichnung = bezeichnung; }

    public String getTyp() { return typ; }
    public void setTyp(String typ) { this.typ = typ; }

    public Integer getKapazitaet() { return kapazitaet; }
    public void setKapazitaet(Integer kapazitaet) { this.kapazitaet = kapazitaet; }
}