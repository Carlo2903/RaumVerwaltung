package de.fhswf.raumverwaltung.ui.dialog.login;

import de.fhswf.raumverwaltung.ui.events.LoginFailedEvent;
import de.fhswf.raumverwaltung.ui.events.LoginSuccessEvent;
import de.fhswf.raumverwaltung.ui.tabpane.MyTabPane;
import de.fhswf.raumverwaltung.MainApp;

import java.util.Observable;
import java.util.Observer;

public class LoginViewModel implements Observer {

    private final LoginModel  model;
    private final LoginView   view;

    public LoginViewModel(LoginView view) {
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
            // Tabs laden
            MyTabPane.getInstance().addTabs();
            // Login-View aus dem MainFrame entfernen
            MainApp.showMainContent();

        } else if (arg instanceof LoginFailedEvent) {
            view.zeigeFehlermeldung();
        }
    }
}