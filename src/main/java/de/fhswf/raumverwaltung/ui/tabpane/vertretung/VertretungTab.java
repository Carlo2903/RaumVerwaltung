package de.fhswf.raumverwaltung.ui.tabpane.vertretung;

import de.fhswf.raumverwaltung.ui.tabpane.MyTab;
import de.fhswf.raumverwaltung.ui.tabpane.Reloadable;
import javafx.scene.control.ScrollPane;

public class VertretungTab extends MyTab implements Reloadable {

    private final VertretungViewModel viewModel;

    public VertretungTab() {
        super("Vertretungen");
        this.viewModel = new VertretungViewModel();

        ScrollPane scroll = new ScrollPane(new VertretungView(viewModel));
        scroll.setFitToWidth(true);
        this.setContent(scroll);
    }

    @Override
    public void reload() {
        viewModel.laden();
    }
}