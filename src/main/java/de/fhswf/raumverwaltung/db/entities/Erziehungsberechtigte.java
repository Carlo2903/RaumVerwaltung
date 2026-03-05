package de.fhswf.raumverwaltung.db.entities;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "erziehungsberechtigte")
public class Erziehungsberechtigte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String beziehung;

    private String adresse;
    private String telefon;
    private String email;

    @ManyToMany
    @JoinTable(
            name = "schueler_eltern",
            joinColumns = @JoinColumn(name = "eltern_id"),
            inverseJoinColumns = @JoinColumn(name = "schueler_id")
    )
    private List<Schueler> schuelerListe;

    public Erziehungsberechtigte() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBeziehung() { return beziehung; }
    public void setBeziehung(String beziehung) { this.beziehung = beziehung; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<Schueler> getSchuelerListe() { return schuelerListe; }
    public void setSchuelerListe(List<Schueler> schuelerListe) { this.schuelerListe = schuelerListe; }
}