package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import javafx.event.EventType;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.function.BiConsumer;

public class Shelf extends HBox {
    private final int shelfId;
    private int size;
    private final HBox content = new HBox();
    private final Circle swapIcon = new Circle(10, Color.WHITE);
    private final BiConsumer<Integer, Integer> callback;
    private Text sizeText;

    public Shelf(ReducedResourceContainer shelf, BiConsumer<Integer, Integer> callback) {
        this.callback = callback;
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

        sizeText = new Text("Size: " + shelf.getSize());
        this.getChildren().add(sizeText);
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

    public void addSwapper() {
        setSwapDnD();
        this.getChildren().add(swapIcon);
    }

    private void setSwapDnD() {
        swapIcon.setOnDragDetected((event -> {
            Dragboard db = this.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString("[swap]" + this.getShelfId() + " size:" + this.getContentSize());
            db.setContent(content);
            event.consume();
        }));

        this.setOnDragOver((event) -> {
            Dragboard db = event.getDragboard();
                if (db.hasString() && db.getString().startsWith("[swap]")
                        && Integer.parseInt(db.getString().substring(6, db.getString().indexOf(" "))) != this.getShelfId()
                        && Integer.parseInt(db.getString().substring(db.getString().indexOf(":") + 1)) <= size) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
            event.consume();
        });

        this.setOnDragDropped((event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString() && db.getString().startsWith("[swap]")) {
                try {
                    int sourceShelfID = Integer.parseInt(db.getString().substring(6, db.getString().indexOf(" ")));

                    callback.accept(sourceShelfID, this.shelfId);

                    // TODO disable temporarily dnd until response is received

                } catch (Exception e) { // TODO remove this catch once debugged
                    e.printStackTrace();
                }
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    public void adjustSize(int size) {
        this.size = size;
        sizeText.setText("Size: " + size);
    }

//    private void disableSwapDnD() {
//        swapIcon.removeEventHandler(EventType.ROOT);
//    }
}
