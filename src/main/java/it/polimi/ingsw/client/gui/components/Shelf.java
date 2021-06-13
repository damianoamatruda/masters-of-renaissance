package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Shelf extends BorderPane {
    private static final Logger LOGGER = Logger.getLogger(Shelf.class.getName());

    private int shelfId;
    private int size;
    private HBox content;
    private final Circle swapIcon = new Circle(10, Color.WHITE);
    private final BiConsumer<Integer, Integer> callback;
    private final Text sizeText;

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
    }

    public void setContent(HBox content) {
        this.content = content;
    }

    public void addResource(Resource r) {
        r.setPreserveRatio(true);
        r.setFitHeight(this.getMinHeight());
        ((HBox) this.getCenter()).getChildren().add(r);
    }

    public void addResource(String resource) {
        Resource r = new Resource();
        r.setResourceType(resource);

        addResource(r);
    }

    public int getShelfId() {
        return shelfId;
    }

    public int getSize() {
        return size;
    }

    public void removeResource() {
        content.getChildren().remove(content.getChildren().size() - 1);
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
        this.setRight(swapIcon);

        BorderPane.setAlignment(swapIcon, Pos.CENTER_RIGHT);
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
                    LOGGER.log(Level.SEVERE, "Unknown exception (TODO: Remove this)", e);
                }
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

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

//    private void disableSwapDnD() {
//        swapIcon.removeEventHandler(EventType.ROOT);
//    }
}
