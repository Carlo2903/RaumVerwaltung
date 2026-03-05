module com.example.raumverwaltung {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.raumverwaltung to javafx.fxml;
    exports com.example.raumverwaltung;
}