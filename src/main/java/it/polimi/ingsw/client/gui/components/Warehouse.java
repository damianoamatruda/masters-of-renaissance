package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

/** Gui component representing a Warehouse. */
public class Warehouse extends VBox {
    private static final double minWidth = 390.0;
    private static final double maxRowWidth = 300.0;
    private final Map<Integer, Shelf> shelves = new HashMap<>();
    private int waitingForSwap1;
    private int waitingForSwap2;

    public Warehouse() {
        this.setPrefWidth(minWidth);
        this.setPrefHeight(maxRowWidth);
        this.setMaxWidth(minWidth);
        this.setMaxHeight(maxRowWidth);
    }

    /**
     * Sets and displays the warehouse shelves.
     *
     * @param shelves   the cached player's shelves
     * @param callback  the callback function (used for shelf swapping)
     * @param wantsDnD  true if drag and drop of resources is needed
     */
    public void setWarehouseShelves(List<ReducedResourceContainer> shelves, BiConsumer<Integer, Integer> callback, boolean wantsDnD) {
        double maxRowHeight = getPrefHeight() / shelves.size();
        getChildren().clear();
        for (ReducedResourceContainer container : shelves) {
            Shelf shelf = new Shelf(container, maxRowHeight, maxRowWidth, callback);
            for (String resource : container.getContent().keySet()) {
                for (int i = 0; i < container.getContent().get(resource); i++) {
                    if (wantsDnD)
                        shelf.addResourceDraggable(resource);
                    else
                        shelf.addResource(resource);
                }
            }
            getChildren().add(shelf);
            this.shelves.put(container.getId(), shelf);
        }
    }

    /**
     * Sets and displays the warehouse shelves, without enabling drag and drop of resources.
     *
     * @param shelves  the cached player's shelves
     * @param callback the callback function (used for shelf swapping)
     */
    public void setWarehouseShelves(List<ReducedResourceContainer> shelves, BiConsumer<Integer, Integer> callback) {
        setWarehouseShelves(shelves, callback, false);
    }

    /**
     * Refreshes the view after removing a resource.
     *
     * @param id the involved shelf ID
     */
    public void refreshShelfRemove(int id, Resource res) {
        shelves.get(id).removeResource(res);
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
        shelves.values().forEach(Shelf::addSwapper);
    }

    /**
     * Swaps the content of two shelves.
     *
     * @param s1              shelf one (source)
     * @param s2              shelf two (destination)
     * @param setResourcesDnD whether the resources can be drag and dropped
     */
    public void swapShelves(Shelf s1, Shelf s2, boolean setResourcesDnD) {
        int index1 = Math.min(getChildren().indexOf(s1), getChildren().indexOf(s2));
        int index2 = Math.max(getChildren().indexOf(s1), getChildren().indexOf(s2));

        Platform.runLater(() -> {
            Node n = getChildren().remove(index2);
            n = getChildren().set(index1, n);
            getChildren().add(index2, n);

            int tempSize = s1.getSize();
            int tempId = s1.getShelfId();

            shelves.put(s1.getShelfId(), s2);
            shelves.put(s2.getShelfId(), s1);

            s1.refresh(s2.getSize(), s2.getShelfId(), setResourcesDnD);
            s2.refresh(tempSize, tempId, setResourcesDnD);
        });
    }

    /**
     * Retrieves the shelf that is bound to a given resource type.
     *
     * @param resource  the resource type
     * @return  the Shelf component
     */
    public Optional<Shelf> getShelfByResource(String resource) {
        return shelves.values().stream().filter(s -> s.getBoundResource() != null && s.getBoundResource().equals(resource)).findAny();
    }

    /**
     * Handles the choice of a resource contained in the warehouse, when paying
     *
     * @param containers      the map of the input of a resource transaction
     * @param newTempShelves  the list of temporary shelves which content to edit on resource click
     */
    public void addResourcesSelector(Map<Integer, Map<String, Integer>> containers, List<ReducedResourceContainer> newTempShelves) {
        shelves.values().forEach(s -> s.addResourcesSelector(containers));
    }

    /**
     * Handles the choice of a resource contained in the warehouse, when paying
     *
     * @param insert
     * @param remove
     */
    public void addResourcesSelector(BiConsumer<String, Integer> insert, BiConsumer<String, Integer> remove) {
        shelves.values().forEach(s -> s.addResourcesSelector(insert, remove));
    }

    public void setWhiteText() {
        shelves.values().forEach(s -> ((Text) ((BorderPane) s.getChildren().get(0)).getChildren().get(0)).setFill(Color.WHITE));
    }
}
