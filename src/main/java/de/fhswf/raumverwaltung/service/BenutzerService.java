package de.fhswf.raumverwaltung.service;

import de.fhswf.raumverwaltung.db.dao.BenutzerDao;
import de.fhswf.raumverwaltung.db.entities.*;
import de.fhswf.raumverwaltung.db.exception.PlanungException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

public class BenutzerService {

    private static BenutzerService instance;
    private final BenutzerDao benutzerDao = new BenutzerDao();

    // Aktuell eingeloggter Benutzer
    private Benutzer aktuellerBenutzer;

    private BenutzerService() {}

    public static BenutzerService getInstance() {
        if (instance == null) {
            instance = new BenutzerService();
        }
        return instance;
    }

    public Benutzer login(String benutzername, String passwort) throws PlanungException {
        String hash = hashPasswort(passwort);

        System.out.println("Benutzername: " + benutzername);
        System.out.println("Hash: " + hash);

        Optional<Benutzer> benutzer = benutzerDao.findeNachLogin(benutzername, hash);

        if (benutzer.isEmpty()) {
            throw new PlanungException("Login fehlgeschlagen",
                    "Benutzername oder Passwort ist falsch.");
        }

        aktuellerBenutzer = benutzer.get();
        return aktuellerBenutzer;
    }

    public void logout() {
        aktuellerBenutzer = null;
    }

    public Benutzer getAktuellerBenutzer() {
        return aktuellerBenutzer;
    }

    public boolean istEingeloggt() {
        return aktuellerBenutzer != null;
    }




    // SHA-256 Hash – reicht für ein Uni-Projekt
    private String hashPasswort(String passwort) throws PlanungException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(passwort.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new PlanungException("Systemfehler", "Passwort konnte nicht verarbeitet werden.");
        }
    }

    public void erstelleBenutzerFallsNichtVorhanden(
            List<Lehrkraft> lehrkraefte, List<Klasse> klassen) throws PlanungException {

        String lehrerPasswortHash   = hashPasswort("lehrer123");
        String schuelerPasswortHash = hashPasswort("schueler123");

        lehrkraefte.forEach(lehrkraft -> {
            String benutzername = lehrkraft.getKuerzel().toLowerCase();

            // Nur nach Benutzername prüfen – nicht nach Passwort
            if (benutzerDao.findeNachBenutzername(benutzername).isEmpty()) {
                benutzerDao.persist(new LehrerBenutzer(
                        benutzername, lehrerPasswortHash, lehrkraft
                ));
            }
        });

        klassen.forEach(klasse -> {
            String benutzername = klasse.getBezeichnung().toLowerCase();

            if (benutzerDao.findeNachBenutzername(benutzername).isEmpty()) {
                benutzerDao.persist(new SchuelerBenutzer(
                        benutzername, schuelerPasswortHash, klasse
                ));
            }
        });
    }
}