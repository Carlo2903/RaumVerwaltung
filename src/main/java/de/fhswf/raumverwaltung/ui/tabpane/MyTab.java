package de.fhswf.raumverwaltung.ui.tabpane;

import javafx.scene.control.Tab;

public class MyTab extends Tab {
    public MyTab() {
        this.setClosable(false);
    }

    public MyTab(String text) {
        super(text);
        this.setClosable(false);
    }
}