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



    exports de.fhswf.raumverwaltung;
    exports de.fhswf.raumverwaltung.ui;
    exports de.fhswf.raumverwaltung.ui.tabpane;
    exports de.fhswf.raumverwaltung.ui.tabpane.raum;

    exports de.fhswf.raumverwaltung.ui.tabpane.lehrkraft;
    exports de.fhswf.raumverwaltung.ui.tabpane.fach;
    exports de.fhswf.raumverwaltung.ui.tabpane.klasse;
    exports de.fhswf.raumverwaltung.ui.dialog.login;
    exports de.fhswf.raumverwaltung.ui.events;
}
