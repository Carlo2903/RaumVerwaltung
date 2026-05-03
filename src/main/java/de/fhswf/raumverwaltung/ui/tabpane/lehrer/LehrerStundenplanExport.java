package de.fhswf.raumverwaltung.ui.tabpane.lehrer;

import de.fhswf.raumverwaltung.MainApp;
import de.fhswf.raumverwaltung.ui.tabpane.MyTab;
import de.fhswf.raumverwaltung.ui.tabpane.Reloadable;
import de.fhswf.raumverwaltung.ui.tabpane.stundenplan.StundenplanRasterView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageOrientation;
import javafx.print.Printer;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public abstract class LehrerStundenplanExport extends MyTab implements Reloadable {


    protected StundenplanRasterView rasterView;

    public LehrerStundenplanExport(String titel) {
        super(titel);
    }


    protected void buildContent(StundenplanRasterView rasterView) {
        this.rasterView = rasterView;

        Button btnExport = new Button("📄 PDF exportieren");
        btnExport.setStyle(
                "-fx-background-color: #3d5a80;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 6 12 6 12;"
        );
        btnExport.setOnAction(e -> exportierePdf());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbar = new HBox(spacer, btnExport);
        toolbar.setPadding(new Insets(8, 16, 8, 16));
        toolbar.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-border-width: 0 0 1 0;"
        );

        BorderPane layout = new BorderPane();
        layout.setTop(toolbar);
        layout.setCenter(rasterView);



        this.setContent(layout);
    }




    private void exportierePdf() {
        javafx.print.PrinterJob job =
                javafx.print.PrinterJob.createPrinterJob();

        if (job == null) {
            new Alert(Alert.AlertType.ERROR,
                    "Kein Drucker verfügbar.").showAndWait();
            return;
        }

        boolean drucken = job.showPrintDialog(MainApp.primaryStage);

        if (drucken) {
            javafx.print.PageLayout pageLayout = job.getPrinter()
                    .createPageLayout(
                            javafx.print.Paper.A4,
                            PageOrientation.PORTRAIT,
                            Printer.MarginType.EQUAL
                    );

            // Skalierung als Transform – nicht als ScaleX/Y Property
            double breite = rasterView.getWidth();
            double hoehe  = rasterView.getHeight();

            if (breite == 0 || hoehe == 0) {
                new Alert(Alert.AlertType.WARNING,
                        "Stundenplan noch nicht vollständig geladen.")
                        .showAndWait();
                return;
            }

            double scaleX = pageLayout.getPrintableWidth()  / breite ;
            double scaleY = pageLayout.getPrintableHeight() / hoehe;
            double scale  = Math.min(scaleX, scaleY);

            javafx.scene.transform.Scale transform =
                    new javafx.scene.transform.Scale(scale, scale);

            // Transform hinzufügen
            rasterView.getTransforms().add(transform);

            boolean erfolg = job.printPage(pageLayout, rasterView);

            // Transform immer entfernen – auch bei Fehler
            rasterView.getTransforms().remove(transform);

            if (erfolg) {
                job.endJob();
            } else {
                new Alert(Alert.AlertType.ERROR,
                        "Drucken fehlgeschlagen.").showAndWait();
            }
        }
    }

    @Override
    public abstract void reload();
}
