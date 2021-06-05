package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class Shelf extends HBox {
    private final int shelfId;
    private final int size;
    private final HBox content = new HBox();

    public Shelf(ReducedResourceContainer shelf) {
        this.shelfId = shelf.getId();
        this.size = shelf.getSize();
        this.setSpacing(20);
        this.setMaxWidth(400);

        content.setPrefHeight(100);
        content.setPrefWidth(300);
        content.setStyle("-fx-background-image: url('/assets/gui/playerboard/warehouseshelf.png');" +
                "-fx-background-position: center center;" +
                "-fx-background-repeat: stretch;" +
                "-fx-alignment: center;" +
                "-fx-opacity: 1;" +
                "-fx-background-size: 300 100;");

        this.getChildren().add(new Text("Size: " + shelf.getSize()));
        this.getChildren().add(content);
    }

    public void addResource(Resource r) {
        ((HBox) this.getChildren().get(1)).getChildren().add(r);
        r.setPreserveRatio(true);
    }

    public void addResource(String resource) {
        Resource r = new Resource();
        r.setResourceType(resource);
        ((HBox) this.getChildren().get(1)).getChildren().add(r);
        r.setPreserveRatio(true);

    }

    public int getShelfId() {
        return shelfId;
    }

    public int getSize() {
        return size;
    }

    public void removeResource() {
        ((HBox) this.getChildren().get(1)).getChildren().remove(((HBox) this.getChildren().get(1)).getChildren().size() - 1);
    }

    public int getContentSize() {
        return content.getChildren().size();
    }

    public String getBoundResource() {
        if (content.getChildren().size() <= 0) return null;
        return ((Resource)content.getChildren().get(0)).getName();
    }
}
