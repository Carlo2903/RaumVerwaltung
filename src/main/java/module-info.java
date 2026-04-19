module de.fhswf.raumverwaltung {
    // Die Basis-Anforderungen (die hattet ihr schon)
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires static lombok;
    requires atlantafx.base;

    // Der VIP-Pass für Hibernate (Datenbank)
    opens de.fhswf.raumverwaltung.db.entities to org.hibernate.orm.core;
    exports de.fhswf.raumverwaltung.db.entities;


    // Erlaubt JavaFX, deine MainApp zu starten
    exports de.fhswf.raumverwaltung;

    // Erlaubt JavaFX, deine Fenster und Tabs zu zeichnen
    exports de.fhswf.raumverwaltung.ui;
    exports de.fhswf.raumverwaltung.ui.tabpane;
    exports de.fhswf.raumverwaltung.ui.tabpane.raum;
}