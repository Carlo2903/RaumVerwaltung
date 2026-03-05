module com.example.raumverwaltung {

    // Deine bisherigen JavaFX-Abhängigkeiten (die stehen da wahrscheinlich schon)
    requires javafx.controls;
    requires javafx.fxml;

    // 1. NEU: Sag dem Projekt, dass es JPA und Hibernate nutzen soll
    requires jakarta.persistence;
    requires org.hibernate.orm.core;

    // 2. NEU: Der VIP-Pass für Hibernate! Das erlaubt den Zugriff auf die privaten Felder (wie 'id')
    opens de.fhswf.raumverwaltung.db.entities to org.hibernate.orm.core;

    // Erlaube anderen Teilen deines Programms den Zugriff auf die Klassen
    exports de.fhswf.raumverwaltung.db.entities;

    // (Deine anderen exports/opens für JavaFX können hier bleiben)
}
