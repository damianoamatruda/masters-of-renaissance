package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;

public class Shelf extends HBox {
    private int shelfId;

    public Shelf(ReducedResourceContainer shelf) {
        this.shelfId = shelf.getId();
    }

    public void addResource(String resource) {
        Resource r = new Resource();
        r.setResourceType(resource);
        this.getChildren().add(r);
        r.setPreserveRatio(true);

        r.setOnDragDetected((event) -> {
                    Dragboard db = r.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.putImage(r.getImage());
                    content.putString(shelfId+"");
                    db.setContent(content);
                    event.consume();
                }
        );
    }

    public int getShelfId() {
        return shelfId;
    }

    public void removeResource() {
        this.getChildren().remove(this.getChildren().size() - 1);
    }
}
