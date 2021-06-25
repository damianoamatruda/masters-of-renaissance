package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.common.events.mvevents.UpdateFaithPoints;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/** Gui component representing a player's playerboard. */
public class Playerboard extends HBox {
    @FXML
    private final HBox canvas;
    @FXML
    private ImageView frontBG;
    @FXML private GridPane board;
    @FXML private GridPane storageColumn;

    private final Warehouse w;
    private final Strongbox s;
    private final Production p;
    private final List<DevSlot> slots;
    private final FaithTrack faithTrack;

    private final double bgPixelWidth = 908;
    private final double bgPixelHeight = 646;
    private final double bgRatio = bgPixelWidth / bgPixelHeight;

    /**
     * Class constructor.
     *
     * @param warehouse     the player's warehouse
     * @param strongbox     the player's strongbox
     * @param production    the base production
     * @param slots         the player's development slots
     * @param faithTrack    the faith track
     */
    public Playerboard(Warehouse warehouse, Strongbox strongbox, Production production, List<DevSlot> slots, FaithTrack faithTrack) {
        this.w = warehouse;
        this.s = strongbox;
        this.p = production;
        this.slots = slots;
        this.faithTrack = faithTrack;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/playerboard.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        setBackground();

        Group wh = new Group(warehouse);

        storageColumn.add(wh, 0, 1);
        storageColumn.add(strongbox, 0, 3);

        StackPane baseProduction = new StackPane();
        ImageView baseProdPaper = new ImageView(new Image("/assets/gui/playerboard/baseproduction.png"));
        baseProdPaper.setFitWidth(110);
        baseProdPaper.setFitHeight(110);
        baseProduction.setMaxHeight(110);

//        baseProduction.setBorder(new Border(new BorderStroke(Color.RED,
//                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        baseProduction.getChildren().add(baseProdPaper);
        baseProduction.getChildren().add(production);

        StackPane.setAlignment(production, Pos.BOTTOM_LEFT);
        board.add(baseProduction, 3, 1);

        HBox devSlotsBox = new HBox();
        devSlotsBox.getChildren().addAll(slots);
        devSlotsBox.setAlignment(Pos.TOP_CENTER);
        slots.forEach(s -> s.setMinWidth(200));

        board.add(devSlotsBox, 4, 1);

        Group g = new Group(faithTrack);
        board.add(g, 1, 0);

        changedSize(null, 0, 0);

        canvas = this;
        this.widthProperty().addListener(this::changedSize);
        this.heightProperty().addListener(this::changedSize);
    }

    /**
     *
     *
     * @param observable
     * @param oldValue
     * @param newValue
     * @param <T>
     */
    private <T> void changedSize(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        double boardWidth = 1012, boardHeight = 720,
               storageColWidth = 224;
        if (this.getHeight() > 0 && this.getWidth() > 0) {
            double newDimRatio = this.getWidth() / this.getHeight();
            double newBoardHeight = newDimRatio > bgRatio ? this.getHeight() : this.getWidth() / bgRatio;
            double newBoardWidth = newDimRatio > bgRatio ? this.getHeight() * bgRatio : this.getWidth();
            board.setMinHeight(newBoardHeight);
            board.setMinWidth(newBoardWidth);
            board.setMaxHeight(newBoardHeight);
            board.setMaxWidth(newBoardWidth);
        }
        if (board.getHeight() > 0 && board.getWidth() > 0) {
            boardWidth = board.getWidth();
            boardHeight = board.getHeight();
            storageColWidth = storageColumn.getWidth();
        }

        frontBG.setFitWidth(boardWidth);
        frontBG.setFitHeight(boardHeight);

        // scalePreservingRatio(w, storageColWidth, w.getPrefWidth() / w.getPrefHeight());
        double whWidth = w.getWidth() > 0 ? w.getWidth() : w.getPrefWidth();
        double whScaleFactor = storageColWidth / whWidth;
        w.setScaleX(whScaleFactor);
        w.setScaleY(whScaleFactor);
        
        scalePreservingRatio(s, storageColWidth, s.getPrefWidth() / s.getPrefHeight());

        scalePreservingRatio(p,
            board.getColumnConstraints().get(3).getPercentWidth() * boardWidth / 100,
            p.getMaxWidth() / p.getMaxHeight());

        for (DevSlot sl : slots) {
            scalePreservingRatio(sl,
                board.getColumnConstraints().get(5).getPercentWidth() * boardWidth / 100,
                sl.getPrefWidth() / sl.getPrefHeight());  
        }
        
        double ftWidth = faithTrack.getWidth() > 0 ? (faithTrack.getWidth()) : 1768;
        double ftScaleFactor = boardWidth / ftWidth;
        faithTrack.setScaleX(ftScaleFactor);
        faithTrack.setScaleY(ftScaleFactor);
    }

    /**
     *
     * @param child
     * @param parentSizeLimitWidth
     * @param componentRatio
     */
    private static void scalePreservingRatio(Pane child, double parentSizeLimitWidth, double componentRatio) {
        child.setPrefWidth(parentSizeLimitWidth);
        child.setPrefHeight(parentSizeLimitWidth / componentRatio);
    }

    /**
     * Sets and displays a background for the playerboard.
     */
    private void setBackground() {
        Image frontBGImage = new Image(
                Objects.requireNonNull(getClass().getResource("/assets/gui/playerboard/background.png")).toExternalForm());

        frontBG.setImage(frontBGImage);
    }

    /**
     * Updates a marker in the faith track.
     *
     * @param event     the event object containing the info
     * @param oldPts    the faith points before update
     */
    public void updateFaithPoints(UpdateFaithPoints event, int oldPts) {
        if (event.isBlackCross())
            faithTrack.updateBlackMarker(event.getFaithPoints(), oldPts);
        else if (Gui.getInstance().getViewModel().getCurrentPlayer().equals(event.getPlayer()))
            faithTrack.updatePlayerMarker(event.getFaithPoints(), oldPts);
    }

    /**
     * Adds the buttons to select the productions to be activated.
     *
     * @param toActivate         the list of productions to be activated
     * @param activateProduction the button to be disabled, if no production is selected
     */
    public void addProduceButtons(List<Integer> toActivate, SButton activateProduction) {
        slots.forEach(slot -> slot.addProduceButton(toActivate, activateProduction));

        p.addProduceButton(toActivate, activateProduction);
    }
}
