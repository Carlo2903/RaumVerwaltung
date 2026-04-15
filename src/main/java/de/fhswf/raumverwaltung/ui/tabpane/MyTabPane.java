package de.fhswf.raumverwaltung.ui.tabpane;

import de.fhswf.raumverwaltung.ui.tabpane.raum.RaumTab;
import javafx.scene.control.TabPane;

/**
 * Das MyTabPane verwaltet alle Arbeitsbereiche der App.
 * Durch das Singleton-Pattern ist es global erreichbar.
 */
public class MyTabPane extends TabPane {

    private static MyTabPane instance = null;

    // Instanzen der einzelnen Fach-Tabs
    private final RaumTab raumTab;
    // Hier kommen später weitere hinzu, z.B.:
    // private final LehrerTab lehrerTab;

    private MyTabPane() {
        // Initialisierung der Fach-Tabs
        this.raumTab = new RaumTab();

        // Standard-Verhalten: Tabs können nicht vom User geschlossen werden
        this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
    }

    /**
     * Singleton-Zugriffspunkt
     */
    public static MyTabPane getInstance() {
        if (instance == null) {
            instance = new MyTabPane();
        }
        return instance;
    }

    /**
     * Entfernt alle sichtbaren Tabs (wird beim Logout aufgerufen).
     */
    public void removeAll() {
        this.getTabs().clear();
    }

    /**
     * Fügt die Tabs hinzu (wird nach erfolgreichem Login aufgerufen).
     */
    public void addTabs() {
        // Sicherstellen, dass keine Duplikate entstehen
        this.removeAll();

        // Hier werden alle Tabs registriert, die Ismail gebaut hat
        this.getTabs().addAll(raumTab);

        // Später: if(currentUser.isAdmin()) { getTabs().add(adminTab); }
    }
}