package de.fhswf.raumverwaltung.service;

import de.fhswf.raumverwaltung.ui.tabpane.Reloadable;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class AutoRefreshService {

    private static AutoRefreshService instance;

    private final List<Reloadable> registrierte = new ArrayList<>();
    private final Timeline         timeline;

    private AutoRefreshService() {
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(30), e -> refreshAlle())
        );
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    public static AutoRefreshService getInstance() {
        if (instance == null) {
            instance = new AutoRefreshService();
        }
        return instance;
    }

    public void registriere(Reloadable reloadable) {
        if (!registrierte.contains(reloadable)) {
            registrierte.add(reloadable);
        }
    }

    public void abmelden(Reloadable reloadable) {
        registrierte.remove(reloadable);
    }

    public void starten() {
        timeline.play();
    }

    public void stoppen() {
        timeline.stop();
    }

    private void refreshAlle() {
        registrierte.forEach(Reloadable::reload);
    }
}