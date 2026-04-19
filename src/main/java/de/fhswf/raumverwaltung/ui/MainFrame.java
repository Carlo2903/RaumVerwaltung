package de.fhswf.raumverwaltung.ui;

import de.fhswf.raumverwaltung.ui.tabpane.MyTabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.MenuBar;

public class MainFrame extends BorderPane {
    public MainFrame() {
        // Oben: Die Menüleiste (Ismail muss hier noch seine MyMenuBar einfügen)
        // setTop(new MyMenuBar());

        // Mitte: Unser zentrales Tab-System
        this.setCenter(MyTabPane.getInstance());
        MyTabPane.getInstance().addTabs();
    }
}