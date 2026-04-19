package de.fhswf.raumverwaltung.ui.dialog.login;

import de.fhswf.raumverwaltung.ui.events.LoginFailedEvent;
import de.fhswf.raumverwaltung.ui.events.LoginSuccessEvent;
import de.fhswf.raumverwaltung.ui.tabpane.MyTabPane;

import java.util.Observable;
import java.util.Observer;

public class LoginViewModel implements Observer {

    private final LoginModel    model;
    private final LoginDialog   view;

    public LoginViewModel(LoginDialog view) {
        this.view  = view;
        this.model = LoginModel.getInstance();
        this.model.addObserver(this);
    }

    public void doLogin(String benutzername, String passwort) {
        model.doLogin(benutzername, passwort);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof LoginSuccessEvent) {
            // Tabs laden und Dialog schließen
            MyTabPane.getInstance().addTabs();
            view.close();

        } else if (arg instanceof LoginFailedEvent) {
            // Fehlermeldung anzeigen und Felder leeren
            view.zeigeFehlermeldung();
        }
    }
}