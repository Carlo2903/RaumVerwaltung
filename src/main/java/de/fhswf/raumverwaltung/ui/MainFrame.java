package de.fhswf.raumverwaltung.ui;

import de.fhswf.raumverwaltung.db.entities.Benutzer;
import de.fhswf.raumverwaltung.service.AutoRefreshService;
import de.fhswf.raumverwaltung.service.BenutzerService;
import de.fhswf.raumverwaltung.ui.dialog.login.LoginView;
import de.fhswf.raumverwaltung.ui.tabpane.MyTabPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public class MainFrame extends StackPane {

    private final LoginView loginView = new LoginView();
    private final BorderPane mainContainer = new BorderPane();
    private final Label userLabel = new Label();

    public MainFrame() {
        MyTabPane tabPane = MyTabPane.getInstance();
        
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_RIGHT);
        header.setPadding(new Insets(10, 20, 10, 20));
        header.setStyle("-fx-background-color: #3d5a80; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");

        Label titelLabel = new Label("Schul-Planer Pro 2026");
        titelLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16;");
        
        userLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("Logout");
        btnLogout.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: white;" +
                "-fx-border-color: white;" +
                "-fx-border-radius: 4;" +
                "-fx-cursor: hand;"
        );
        
        btnLogout.setOnMouseEntered(e -> btnLogout.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 4; -fx-cursor: hand;"));
        btnLogout.setOnMouseExited(e -> btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 4; -fx-cursor: hand;"));
        btnLogout.setOnAction(e -> handleLogout());

        header.getChildren().addAll(titelLabel, spacer, userLabel, btnLogout);

        mainContainer.setTop(header);
        mainContainer.setCenter(tabPane);
        
        StackPane.setAlignment(mainContainer, Pos.TOP_LEFT);

        this.getChildren().addAll(mainContainer, loginView);
        this.setMaxWidth(Double.MAX_VALUE);
        this.setMaxHeight(Double.MAX_VALUE);
    }

    public void hideLogin() {
        Benutzer b = BenutzerService.getInstance().getAktuellerBenutzer();
        if (b != null) {
            String roleName = b.getRolle().name().substring(0, 1) + 
                              b.getRolle().name().substring(1).toLowerCase();
            userLabel.setText(b.getBenutzername() + " (" + roleName + ")");
        }
        this.getChildren().remove(loginView);
    }
    
    private void handleLogout() {
        BenutzerService.getInstance().logout();
        
        AutoRefreshService.getInstance().stoppen();
        
        MyTabPane.getInstance().removeAll();
        
        loginView.clearInputs();
        if (!this.getChildren().contains(loginView)) {
            this.getChildren().add(loginView);
        }
    }
}