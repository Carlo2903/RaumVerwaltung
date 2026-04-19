package de.fhswf.raumverwaltung.ui;

import de.fhswf.raumverwaltung.ui.dialog.login.LoginView;
import de.fhswf.raumverwaltung.ui.tabpane.MyTabPane;
import javafx.scene.layout.StackPane;

public class MainFrame extends StackPane {

    private final LoginView loginView = new LoginView();

    public MainFrame() {
        // TabPane liegt unten, LoginView als Overlay drüber
        this.getChildren().addAll(MyTabPane.getInstance(), loginView);
    }

    // Wird nach erfolgreichem Login aufgerufen
    public void hideLogin() {
        this.getChildren().remove(loginView);
    }
}