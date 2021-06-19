package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

/** Gui component representing a Warehouse. */
public class Warehouse extends VBox {
    private static final double minWidth = 360.0;
    private static final double minHeight = 300.0;
    private double maxRowHeight;
    private final Map<Integer, Shelf> shelves = new HashMap<>();

    private int waitingForSwap1, waitingForSwap2;

    public Warehouse() {
        this.setPrefWidth(minWidth);
        this.setPrefHeight(minHeight);
    }

    /**
     * Sets and displays the warehouse shelves, without enabling drag and drop of resources.
     *
     * @param shelves   the cached player's shelves
     * @param callback  the callback function (used for shelf swapping)
     */
    public void setWarehouseShelves(List<ReducedResourceContainer> shelves, BiConsumer<Integer, Integer> callback) {
        setWarehouseShelves(shelves, callback, false);
    }

    /**
     * Sets and displays the warehouse shelves.
     *
     * @param shelves   the cached player's shelves
     * @param callback  the callback function (used for shelf swapping)
     * @param wantsDnD  true if drag and drop of resources is needed
     */
    public void setWarehouseShelves(List<ReducedResourceContainer> shelves, BiConsumer<Integer, Integer> callback, boolean wantsDnD) {
        this.getChildren().clear();
        maxRowHeight = getPrefHeight() / shelves.size();
        if(shelves != null) {
            for (ReducedResourceContainer shelf : shelves) {
                Shelf content = new Shelf(shelf, maxRowHeight, minHeight, callback);

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
     * Adds a resource to a shelf, also enabling its drag and drop.
     *
     * @param id        the destination shelf ID
     * @param resource  the resource type
     */
    public void addResourceDraggable(int id, String resource) {
        Shelf shelf = shelves.get(id);
        Resource r = new Resource(resource);
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
     * Refreshes the view after removing a resource.
     *
     * @param id the involved shelf ID
     */
    public void refreshShelfRemove(int id) {
        Shelf shelf = shelves.get(id);
        shelf.removeResource();
    }

    /**
     * Retrieves a contained shelf by the ID.
     *
     * @param id    the shelf ID
     * @return  the Shelf component
     */
    public Shelf getShelf(int id) {
        return shelves.get(id);
    }

    /**
     * Marks two shelves enqueued for swap, until a response is received.
     *
     * @param s1    shelf one (source)
     * @param s2    shelf two (destination)
     */
    public void setWaitingForSwap(Integer s1, Integer s2) {
        waitingForSwap1 = s1;
        waitingForSwap2 = s2;
    }

    /**
     * Gets the source shelf of swap.
     *
     * @return shelf one
     */
    public int getWaitingForSwap1() {
        return waitingForSwap1;
    }

    /**
     * Gets the destination shelf of swap.
     *
     * @return shelf two
     */
    public int getWaitingForSwap2() {
        return waitingForSwap2;
    }

    /**
     * Adds an icon that enables swapping shelves.
     */
    public void enableSwapper() {
        for(Shelf shelf : shelves.values())
            shelf.addSwapper();
    }

    /**
     * Swaps the content of two shelves.
     *
     * @param s1    shelf one (source)
     * @param s2    shelf two (destination)
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
     * Retrieves the shelf that is bound to a given resource type.
     *
     * @param resource  the resource type
     * @return  the Shelf component
     */
    public Optional<Shelf> getShelfByResource(String resource) {
        return shelves.values().stream().filter(sh -> sh.getBoundResource() != null && sh.getBoundResource().equals(resource)).findAny();
    }

    /**
     * Handles the choice of a resource contained in the warehouse, when paying
     *
     * @param containers      the map of the input of a resource transaction
     * @param newTempShelves  the list of temporary shelves which content to edit on resource click
     */
    public void addResourcesSelector(Map<Integer, Map<String, Integer>> containers, List<ReducedResourceContainer> newTempShelves) {
        for(Shelf s : shelves.values()) {
            Optional<ReducedResourceContainer> reduced = newTempShelves.stream().filter(red -> red.getId() == s.getShelfId()).findAny();
            reduced.ifPresent(value -> s.addResourcesSelector(containers, value));
        }
    }

    /**
     * Handles the choice of a resource contained in the warehouse, when paying
     *
     * @param insert
     * @param remove
     */
    public void addResourcesSelector(BiConsumer<String, Integer> insert, BiConsumer<String, Integer> remove) {
        for(Shelf s : shelves.values()) {
            s.addResourcesSelector(insert, remove);
        }
    }
}
