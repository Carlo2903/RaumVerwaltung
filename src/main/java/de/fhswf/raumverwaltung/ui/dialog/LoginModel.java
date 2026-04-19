package de.fhswf.raumverwaltung.ui.dialog.login;

import de.fhswf.raumverwaltung.db.entities.Benutzer;
import de.fhswf.raumverwaltung.db.exception.PlanungException;
import de.fhswf.raumverwaltung.service.BenutzerService;
import de.fhswf.raumverwaltung.ui.events.LoginFailedEvent;
import de.fhswf.raumverwaltung.ui.events.LoginSuccessEvent;
import de.fhswf.raumverwaltung.ui.events.LoggedOutEvent;
import lombok.Getter;

import java.util.Observable;

public class LoginModel extends Observable {

    private static LoginModel instance;

    private final BenutzerService benutzerService = BenutzerService.getInstance();

    @Getter
    private Benutzer aktuellerBenutzer = null;

    private LoginModel() {}

    public static LoginModel getInstance() {
        if (instance == null) {
            instance = new LoginModel();
        }
        return instance;
    }

    public void doLogin(String benutzername, String passwort) {
        try {
            aktuellerBenutzer = benutzerService.login(benutzername, passwort);
            setChanged();
            notifyObservers(new LoginSuccessEvent());
        } catch (PlanungException e) {
            setChanged();
            notifyObservers(new LoginFailedEvent());
        }
    }

    public void logout() {
        benutzerService.logout();
        aktuellerBenutzer = null;
        setChanged();
        notifyObservers(new LoggedOutEvent());
    }
}