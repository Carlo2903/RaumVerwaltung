module com.example.raumverwaltung {
    requires javafx.controls;
    requires javafx.fxml;


    opens de.fhswf.raumverwaltung to javafx.fxml;
    exports de.fhswf.raumverwaltung;
}