package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Shelf extends BorderPane {
    private static final Logger LOGGER = Logger.getLogger(Shelf.class.getName());

    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

    private int shelfId;
    private int size;
    private HBox content;
    private final Circle swapIcon = new Circle(10, Color.WHITE);
    private final BiConsumer<Integer, Integer> callback;
    private final Text sizeText;

    /**
     *
     * @param shelf
     * @param maxHeight
     * @param contentWidth
     * @param callback
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

        sizeText = new Text("Size: " + shelf.getSize());
        // content.setBorder(new Border(new BorderStroke(Color.PINK,
        //     BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        this.setLeft(sizeText);
        this.setCenter(content);
        
        BorderPane.setMargin(content, new Insets(0, 10, 0, 10));
        BorderPane.setAlignment(sizeText, Pos.CENTER_LEFT);
        this.setPickOnBounds(false);
    }

    /**
     *
     * @param content
     */
    public void setContent(HBox content) {
        this.content = content;
    }

    /**
     *
     * @param r
     */
    public void addResource(Resource r) {
        r.setPreserveRatio(true);
        r.setFitHeight(this.getMinHeight());
        ((HBox) this.getCenter()).getChildren().add(r);
    }

    /**
     *
     * @param resource
     */
    public void addResource(String resource) {
        Resource r = new Resource();
        r.setResourceType(resource);

        addResource(r);
    }

    /**
     *
     * @return
     */
    public int getShelfId() {
        return shelfId;
    }

    /**
     *
     * @return
     */
    public int getSize() {
        return size;
    }

    /**
     *
     */
    public void removeResource() {
        content.getChildren().remove(content.getChildren().size() - 1);
    }

    /**
     *
     * @return
     */
    public int getContentSize() {
        return content.getChildren().size();
    }

    /**
     *
     * @return
     */
    public String getBoundResource() {
        if (content.getChildren().size() <= 0) return null;
        return ((Resource)content.getChildren().get(0)).getName();
    }

    /**
     *
     */
    public void addSwapper() {
        setSwapDnD();
        this.setRight(swapIcon);

        BorderPane.setAlignment(swapIcon, Pos.CENTER_RIGHT);
    }

    /**
     *
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
                try {
                    int sourceShelfID = Integer.parseInt(db.getString().substring(6, db.getString().indexOf(" ")));

                    callback.accept(sourceShelfID, this.shelfId);

                    // TODO disable temporarily dnd until response is received

                } catch (Exception e) { // TODO remove this catch once debugged
                    LOGGER.log(Level.SEVERE, "Unknown exception (TODO: Remove this)", e);
                }
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    /**
     *
     * @param size
     * @param newId
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
     *
     * @param containers
     * @param reducedShelf
     */
    public void addResourcesSelector(Map<Integer, Map<String, Integer>> containers, ReducedResourceContainer reducedShelf) {
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

//    private void disableSwapDnD() {
//        swapIcon.removeEventHandler(EventType.ROOT);
//    }
}
