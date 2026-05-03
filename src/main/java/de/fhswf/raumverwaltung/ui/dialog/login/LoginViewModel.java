package de.fhswf.raumverwaltung.ui.dialog.login;

import de.fhswf.raumverwaltung.ui.events.LoginFailedEvent;
import de.fhswf.raumverwaltung.ui.events.LoginSuccessEvent;
import de.fhswf.raumverwaltung.ui.tabpane.MyTabPane;
import de.fhswf.raumverwaltung.MainApp;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

import java.util.Observable;
import java.util.Observer;

public class LoginViewModel implements Observer {

    private final LoginModel model;

    /**
     * Enthält eine Fehlermeldung wenn der Login fehlschlug, sonst null.
     * Die View bindet sich auf diese Property – das ViewModel kennt die View nicht.
     */
    @Getter
    private final StringProperty fehlerProperty = new SimpleStringProperty();

    public LoginViewModel() {
        this.model = LoginModel.getInstance();
        this.model.addObserver(this);
    }

    public void doLogin(String benutzername, String passwort) {
        fehlerProperty.set(null);          // Vorherige Fehlermeldung zurücksetzen
        model.doLogin(benutzername, passwort);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof LoginSuccessEvent) {
            // Tabs laden und Login-Maske entfernen
            MyTabPane.getInstance().addTabs();
            MainApp.showMainContent();

        } else if (arg instanceof LoginFailedEvent) {
            fehlerProperty.set("Benutzername oder Passwort ist falsch.");
        }
    }
}