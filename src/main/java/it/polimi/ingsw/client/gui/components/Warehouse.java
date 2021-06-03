package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Warehouse extends VBox {
    private final int maxRowHeight;
    private final Map<Integer, Shelf> shelves = new HashMap<>();

    public Warehouse() {
        maxRowHeight = 100; //TODO parameterize
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/warehouse.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setWarehouseShelves(List<ReducedResourceContainer> shelves) {
        if(shelves != null) {
            for (ReducedResourceContainer shelf : shelves) {
                Shelf content = new Shelf(shelf);
                content.setAlignment(Pos.CENTER);

                for(String resource : shelf.getContent().keySet()) {
                    HBox entry = new HBox();

                    entry.setAlignment(Pos.CENTER);

                    entry.maxHeight(maxRowHeight);

                    for(int i = 0; i < shelf.getContent().get(resource); i++) {
                        content.addResource(resource);
                    }

                    content.getChildren().add(entry);

                }
                this.shelves.put(shelf.getId(), content);
                this.getChildren().add(content);
            }
        }
    }

    public void refreshShelfAdd(int id, String resource) {
        Shelf shelf = shelves.get(id);
        shelf.addResource(resource);
    }

    public void refreshShelfRemove(int id, String resource) {
        Shelf shelf = shelves.get(id);
        shelf.removeResource();
    }
}
