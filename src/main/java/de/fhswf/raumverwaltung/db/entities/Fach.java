package de.fhswf.raumverwaltung.db.entities;
import jakarta.persistence.*;

@Entity
@Table(name = "faecher")
public class Fach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bezeichnung;

    @Column(unique = true, nullable = false, length = 10)
    private String kuerzel;

    @Column(name = "wochenstunden_pro_klasse", nullable = false)
    private int wochenstunden;

    @Column(name = "erforderlicher_raumtyp")
    private String erforderlicherRaumtyp;

    public Fach() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBezeichnung() { return bezeichnung; }
    public void setBezeichnung(String bezeichnung) { this.bezeichnung = bezeichnung; }

    public String getKuerzel() { return kuerzel; }
    public void setKuerzel(String kuerzel) { this.kuerzel = kuerzel; }

    public int getWochenstunden() { return wochenstunden; }
    public void setWochenstunden(int wochenstunden) { this.wochenstunden = wochenstunden; }

    public String getErforderlicherRaumtyp() { return erforderlicherRaumtyp; }
    public void setErforderlicherRaumtyp(String erforderlicherRaumtyp) { this.erforderlicherRaumtyp = erforderlicherRaumtyp; }
}