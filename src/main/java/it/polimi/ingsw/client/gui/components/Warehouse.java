package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public class Warehouse extends VBox {
    private double maxRowHeight;
    private final Map<Integer, Shelf> shelves = new HashMap<>();
    
    private int waitingForSwap1, waitingForSwap2;

    /**
     *
     */
    public Warehouse() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/warehouse.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        // this.setBorder(new Border(new BorderStroke(Color.RED,
        //     BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    }

    /**
     *
     * @param shelves
     * @param callback
     */
    public void setWarehouseShelves(List<ReducedResourceContainer> shelves, BiConsumer<Integer, Integer> callback) {
        setWarehouseShelves(shelves, callback, false);
    }

    /**
     *
     * @param shelves
     * @param callback
     * @param wantsDnD
     */
    public void setWarehouseShelves(List<ReducedResourceContainer> shelves, BiConsumer<Integer, Integer> callback, boolean wantsDnD) {
        this.getChildren().clear();
        maxRowHeight = getPrefHeight() / shelves.size();
        if(shelves != null) {
            for (ReducedResourceContainer shelf : shelves) {
                Shelf content = new Shelf(shelf, maxRowHeight, this.getMinHeight(), callback);
                // content.setAlignment(Pos.CENTER);

                // content.setBorder(new Border(new BorderStroke(Color.GREEN,
                //     BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                this.shelves.put(shelf.getId(), content);
                for(String resource : shelf.getContent().keySet()) {
                    HBox entry = new HBox();

                    entry.setAlignment(Pos.CENTER);

                    entry.maxHeight(maxRowHeight);

                    for(int i = 0; i < shelf.getContent().get(resource); i++) {
                        if(wantsDnD)
                            addResourceDraggable(content.getShelfId(), resource);
                        else
                            content.addResource(resource);
                    }
                }
                this.getChildren().add(content);
            }
        }
    }

    /**
     *
     * @param id
     * @param resource
     */
    public void addResourceDraggable(int id, String resource) {
        Shelf shelf = shelves.get(id);
        Resource r = new Resource();
        r.setResourceType(resource);
        shelf.addResource(r);

        r.setOnDragDetected((event) -> {
                Dragboard db = r.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putImage(r.getImage());
                content.putString(shelf.getShelfId()+"");
                db.setContent(content);
                event.consume();
            }
        );
    }

    /**
     *
     * @param id
     */
    public void refreshShelfRemove(int id) {
        Shelf shelf = shelves.get(id);
        shelf.removeResource();
    }

    /**
     *
     * @param id
     * @return
     */
    public Shelf getShelf(int id) {
        return shelves.get(id);
    }

    /**
     *
     * @param s1
     * @param s2
     */
    public void setWaitingForSwap(Integer s1, Integer s2) {
        waitingForSwap1 = s1;
        waitingForSwap2 = s2;
    }

    /**
     *
     * @return
     */
    public int getWaitingForSwap1() {
        return waitingForSwap1;
    }

    /**
     *
     * @return
     */
    public int getWaitingForSwap2() {
        return waitingForSwap2;
    }

    /**
     *
     */
    public void enableSwapper() {
        for(Shelf shelf : shelves.values())
            shelf.addSwapper();
    }

    /**
     *
     * @param s1
     * @param s2
     */
    public void swapShelves(Shelf s1, Shelf s2) {
        int tempIndex1 = this.getChildren().indexOf(s1);
        int tempIndex2 = this.getChildren().indexOf(s2);
        Platform.runLater(() -> {
            this.getChildren().remove(Math.max(tempIndex1, tempIndex2));
            this.getChildren().remove(Math.min(tempIndex1, tempIndex2));

            if(tempIndex1 < tempIndex2) {
                this.getChildren().add(tempIndex1, s2);
                this.getChildren().add(tempIndex2, s1);
            } else {
                this.getChildren().add(tempIndex2, s1);
                this.getChildren().add(tempIndex1, s2);
            }

            int tempSize = s1.getSize();
            int tempId = s1.getShelfId();

            shelves.put(s1.getShelfId(), s2);
            shelves.put(s2.getShelfId(), s1);

            s1.refresh(s2.getSize(), s2.getShelfId());
            s2.refresh(tempSize, tempId);

        });
    }

    /**
     *
     * @param resource
     * @return
     */
    public Optional<Shelf> getShelfByResource(String resource) {
        return shelves.values().stream().filter(sh -> sh.getBoundResource() != null && sh.getBoundResource().equals(resource)).findAny();
    }

    /**
     *
     * @param containers
     * @param newTempShelves
     */
    public void addResourcesSelector(Map<Integer, Map<String, Integer>> containers, List<ReducedResourceContainer> newTempShelves) {
        for(Shelf s : shelves.values()) {
            Optional<ReducedResourceContainer> reduced = newTempShelves.stream().filter(red -> red.getId() == s.getShelfId()).findAny();
            reduced.ifPresent(value -> s.addResourcesSelector(containers, value));
        }
    }
}
