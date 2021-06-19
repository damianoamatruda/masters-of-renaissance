package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

/** Gui component representing a warehouse shelf. */
public class Shelf extends BorderPane {
    private static final Logger LOGGER = Logger.getLogger(Shelf.class.getName());

    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

    private int shelfId;
    private int size;
    private HBox content;
    private final Circle swapIcon = new Circle(10, Color.WHITE);
    private final BiConsumer<Integer, Integer> callback;
    private final Text sizeText;
    private boolean hasPlaceholder;
    private boolean isLeaderDepot;

    /**
     * Class constructor.
     *
     * @param shelf         the cached shelf
     * @param maxHeight     the max height dimension of the component
     * @param contentWidth  the max width dimension of the content in the component
     * @param callback      the callback function (used for shelf swapping)
     */
    public Shelf(ReducedResourceContainer shelf, double maxHeight, double contentWidth, BiConsumer<Integer, Integer> callback) {
        this.callback = callback;
        this.shelfId = shelf.getId();
        this.size = shelf.getSize();
        // this.setSpacing(10);
        // this.setMinWidth(contentWidth);
        this.setMinHeight(maxHeight);
        this.setMaxHeight(maxHeight);
        // this.setAlignment(Pos.CENTER_LEFT);

        content = new HBox();
        content.setMinHeight(maxHeight);
        content.setMaxHeight(maxHeight);
        content.setMinWidth(contentWidth);
        content.setStyle("-fx-background-image: url('/assets/gui/playerboard/warehouseshelf.png');" +
                "-fx-background-position: center center;" +
                "-fx-background-repeat: stretch;" +
                "-fx-alignment: center;" +
                "-fx-opacity: 1;" +
                "-fx-background-size: 300 100;");
        BorderPane.setMargin(content, new Insets(0, 10, 0, 10));

        sizeText = new Text(String.format("Size: %d", shelf.getSize()));

        BorderPane sizePane = new BorderPane();
        sizePane.setMinWidth(70);
        sizePane.setMaxWidth(70);
        sizePane.setMinHeight(maxHeight);
        sizePane.setMaxHeight(maxHeight);
        sizePane.setCenter(sizeText);

        this.setLeft(sizePane);
        this.setCenter(content);

        this.setPickOnBounds(false);
    }

    /**
     * Class constructor.
     *
     * @param shelf         the cached shelf
     * @param maxHeight     the max height dimension of the component
     * @param contentWidth  the max width dimension of the content in the component
     * @param isLeaderDepot true if belonging to a leader card
     */
    public Shelf(ReducedResourceContainer shelf, double maxHeight, double contentWidth, boolean isLeaderDepot) {
        this(shelf, maxHeight, contentWidth, (a, b) -> {});
        this.isLeaderDepot = isLeaderDepot;
    }

    /**
     * Sets and displays the shelf content.
     *
     * @param content the content, as HBox component
     */
    public void setContent(HBox content) {
        this.content = content;
    }

    /**
     * Adds a Resource component to the shelf content
     *
     * @param r the resource as Gui component
     */
    public void addResource(Resource r) {
        removePlaceholder();
        r.setPreserveRatio(true);
        r.setFitHeight(this.getMinHeight());
        ((HBox) this.getCenter()).getChildren().add(r);
    }

    /**
     * Adds a Resource component to the shelf content
     *
     * @param resource the resource type
     */
    public void addResource(String resource) {
        removePlaceholder();
        Resource r = new Resource();
        r.setResourceType(resource);

        addResource(r);
    }

    /**
     * Getter of the shelf ID.
     *
     * @return the shelf ID
     */
    public int getShelfId() {
        return shelfId;
    }

    /**
     * Getter of the shelf size.
     *
     * @return the shelf size
     */
    public int getSize() {
        return size;
    }

    /**
     * Removes a resource from the shelf
     */
    public void removeResource() {
        content.getChildren().remove(content.getChildren().size() - 1);
        if(isLeaderDepot)
            setPlaceholder(getBoundResource());
    }

    /**
     * Getter of the quantity of resources currently stored.
     *
     * @return the quantity of stored resources
     */
    public int getContentSize() {
        return content.getChildren().size();
    }

    /**
     * Getter of the shelf's bound resource type (non null if non empty shelf).
     *
     * @return the shelf's bound resource type
     */
    public String getBoundResource() {
        if (content.getChildren().size() <= 0 && !isLeaderDepot) return null;
        if(isLeaderDepot) return Gui.getInstance().getViewModel().getContainer(shelfId).flatMap(ReducedResourceContainer::getBoundedResType).orElse(null);
        return ((Resource)content.getChildren().get(0)).getName();
    }

    /**
     * Adds an icon that enables shelves swapping.
     */
    public void addSwapper() {
        setSwapDnD();
        this.setRight(swapIcon);

        BorderPane.setAlignment(swapIcon, Pos.CENTER_RIGHT);
    }

    /**
     * Links a swap shelves event handler to the swap icon.
     */
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
                    int sourceShelfID = Integer.parseInt(db.getString().substring(6, db.getString().indexOf(" ")));

                    callback.accept(sourceShelfID, this.shelfId);

                    // TODO disable temporarily dnd until response is received (?)
                }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    /**
     * Refreshes the shelf after a swap, so that only the content is swapped.
     *
     * @param size the shelf max size
     * @param newId the ID of the shelf
     */
    public void refresh(int size, int newId) {
        //adjust size
        this.size = size;
        sizeText.setText("Size: " + size);
        this.shelfId = newId;

        //adjust clipboard content of resources
        for(Node r : content.getChildren()) {
            //TODO handle duplicated code
            r.setOnDragDetected((event) -> {
                    Dragboard db = r.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.putImage(((Resource) r).getImage());
                    content.putString(newId+"");
                    db.setContent(content);
                    event.consume();
                }
            );
        }
    }

    /**
     * Handles the choice of a resource contained in this shelf, when paying
     *
     * @param containers    the map of the input of a resource transaction
     * @param reducedShelf  the cached shelf which content to edit on resource click
     */
    public void addResourcesSelector(Map<Integer, Map<String, Integer>> containers, ReducedResourceContainer reducedShelf) {
        if(Gui.getInstance().getViewModel().getContainer(shelfId).orElseThrow().getContent().size() > 0)
            for (Node r : content.getChildren()) {
                r.setOnMouseClicked(e -> {
                    String name = ((Resource) r).getName();
                    // doesn't work? => r.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, !r.getPseudoClassStates().contains(SELECTED_PSEUDO_CLASS));

                    if(r.getOpacity() != 0.5) {
                        r.setOpacity(0.5);
                        reducedShelf.getContent().put(name, reducedShelf.getContent().get(name) - 1);
                        if (containers.get(shelfId) == null) {
                            Map<String, Integer> content = new HashMap<>();
                            content.put(name, 1);
                            containers.put(shelfId, content);
                        } else if (containers.get(shelfId).get(name) == null) {
                            containers.get(shelfId).put(name, 1);
                        } else {
                            containers.get(shelfId).put(name, containers.get(shelfId).get(name) + 1);
                        }
                    } else {
                        r.setOpacity(1);
                        reducedShelf.getContent().put(name, reducedShelf.getContent().get(name) + 1);
                        containers.get(shelfId).put(name, containers.get(shelfId).get(name) - 1);
                        if (containers.get(shelfId).get(name) == 0)
                            containers.get(shelfId).remove(name);
                        if (containers.get(shelfId).keySet().size() == 0)
                            containers.remove(shelfId);
                    }
                });
            }
    }

    public void addResourcesSelector(BiConsumer<String, Integer> insert, BiConsumer<String, Integer> remove) {
        for (Node r : content.getChildren()) {
            r.setOnMouseClicked(e -> {
                String name = ((Resource) r).getName();
                if (r.getOpacity() != 0.5) {
                    r.setOpacity(0.5);
                    insert.accept(name, shelfId);
                } else {
                    r.setOpacity(1);
                    remove.accept(name, shelfId);
                }
            });
        }
    }

    public void setPlaceholder(String resource) {
        this.hasPlaceholder = true;
        ImageView img = new ImageView(new Image(getResourcePlaceholderPath(resource)));

        img.setFitHeight(30);
        img.setFitWidth(30);
        content.getChildren().add(img);
    }

    /**
     * Getter of the path to the PNG of the depot placeholder.
     *
     * @param resourceType  the depot's bound resource type
     * @return  the path to the PNG resource representing a depot placeholder
     */
    private String getResourcePlaceholderPath(String resourceType) {
        return String.format("/assets/gui/leadertemplates/%sdepot.png", resourceType.toLowerCase());
    }

    private void removePlaceholder() {
        Optional<ReducedResourceContainer> container = Gui.getInstance().getViewModel().getContainer(shelfId);
        container.ifPresent(c -> {
            if(content.getChildren().size() == 1 && c.getContent().size() == 0) {
                this.hasPlaceholder = false;
                content.getChildren().clear();
            }
        });
    }

    public void addResourceDraggable(String resource) {
        //TODO duplicated in Warehouse
        Resource r = new Resource();
        r.setResourceType(resource);
        this.addResource(r);

        r.setOnDragDetected((event) -> {
                    Dragboard db = r.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.putImage(r.getImage());
                    content.putString(this.getShelfId()+"");
                    db.setContent(content);
                    event.consume();
                }
        );
    }

//    private void disableSwapDnD() {
//        swapIcon.removeEventHandler(EventType.ROOT);
//    }
}
