package de.fhswf.raumverwaltung.db.entities;
import jakarta.persistence.*;

@Entity
@Table(name = "stundenplan")
public class StundenplanEintrag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String wochentag; // Montag, Dienstag, etc.

    @Column(nullable = false)
    private int zeitslot; // Wert von 1 bis 6

    @ManyToOne
    @JoinColumn(name = "klasse_id", nullable = false)
    private Klasse klasse;

    @ManyToOne
    @JoinColumn(name = "fach_id", nullable = false)
    private Fach fach;

    @ManyToOne
    @JoinColumn(name = "lehrer_id", nullable = false)
    private Lehrkraft lehrer;

    @ManyToOne
    @JoinColumn(name = "raum_id", nullable = false)
    private Raum raum;

    @Column(name = "ist_vertretung")
    private boolean istVertretung = false;

    @ManyToOne
    @JoinColumn(name = "vertretung_fuer_id")
    private Lehrkraft vertretungFuer;

    private String bemerkung; // "Krankheit", "Raumwechsel"

    public StundenplanEintrag() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getWochentag() { return wochentag; }
    public void setWochentag(String wochentag) { this.wochentag = wochentag; }

    public int getZeitslot() { return zeitslot; }
    public void setZeitslot(int zeitslot) { this.zeitslot = zeitslot; }

    public Klasse getKlasse() { return klasse; }
    public void setKlasse(Klasse klasse) { this.klasse = klasse; }

    public Fach getFach() { return fach; }
    public void setFach(Fach fach) { this.fach = fach; }

    public Lehrkraft getLehrer() { return lehrer; }
    public void setLehrer(Lehrkraft lehrer) { this.lehrer = lehrer; }

    public Raum getRaum() { return raum; }
    public void setRaum(Raum raum) { this.raum = raum; }

    public boolean isIstVertretung() { return istVertretung; }
    public void setIstVertretung(boolean istVertretung) { this.istVertretung = istVertretung; }

    public Lehrkraft getVertretungFuer() { return vertretungFuer; }
    public void setVertretungFuer(Lehrkraft vertretungFuer) { this.vertretungFuer = vertretungFuer; }

    public String getBemerkung() { return bemerkung; }
    public void setBemerkung(String bemerkung) { this.bemerkung = bemerkung; }
}