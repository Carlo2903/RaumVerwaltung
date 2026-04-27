package de.fhswf.raumverwaltung.ui;

import de.fhswf.raumverwaltung.ui.dialog.login.LoginView;
import de.fhswf.raumverwaltung.ui.tabpane.MyTabPane;
import javafx.scene.layout.StackPane;

public class MainFrame extends StackPane {

    private final LoginView loginView = new LoginView();

    public MainFrame() {
        MyTabPane tabPane = MyTabPane.getInstance();
        // TabPane liegt unten, LoginView als Overlay drüber

        // NEU: TabPane füllt den kompletten verfügbaren Platz
        StackPane.setAlignment(tabPane, javafx.geometry.Pos.TOP_LEFT);
        tabPane.setMaxWidth(Double.MAX_VALUE);
        tabPane.setMaxHeight(Double.MAX_VALUE);

        this.getChildren().addAll(tabPane, loginView);
        this.setMaxWidth(Double.MAX_VALUE);
        this.setMaxHeight(Double.MAX_VALUE);
    }

    public void hideLogin() {
        this.getChildren().remove(loginView);
    }
}