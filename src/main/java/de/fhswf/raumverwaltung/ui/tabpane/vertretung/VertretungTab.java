package de.fhswf.raumverwaltung.ui.tabpane.vertretung;

import de.fhswf.raumverwaltung.ui.tabpane.MyTab;
import javafx.scene.control.ScrollPane;

public class VertretungTab extends MyTab {

    public VertretungTab() {
        super("Vertretungen");

        ScrollPane scroll = new ScrollPane(new VertretungView());
        scroll.setFitToWidth(true);
        this.setContent(scroll);
    }
}