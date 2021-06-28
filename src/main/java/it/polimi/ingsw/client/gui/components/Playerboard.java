package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.common.events.mvevents.UpdateFaithPoints;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
    private ImageView frontBG;
    private final List<DevSlot> devSlots;
    @FXML
    private GridPane board;
    @FXML
    private GridPane storageColumn;
    private Warehouse warehouse;
    private Strongbox strongbox;
    private Production production;
    private final FaithTrack faithTrack;

    private final double bgPixelWidth = 908;
    private final double bgPixelHeight = 646;
    private final double bgRatio = bgPixelWidth / bgPixelHeight;

    /**
     * Class constructor.
     *  @param warehouse  the player's warehouse
     * @param strongbox  the player's strongbox
     * @param production the base production
     * @param devSlots   the player's development slots
     * @param faithTrack the faith track
     * @param hasInkwell
     */
    public Playerboard(Warehouse warehouse, Strongbox strongbox, Production production, List<DevSlot> devSlots, FaithTrack faithTrack, boolean hasInkwell) {
        this.devSlots = devSlots;
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

        VBox middleBox = new VBox();
        middleBox.setAlignment(Pos.CENTER);
        middleBox.setSpacing(30);
        board.add(middleBox, 3, 1);

//        middleBox.setBorder(new Border(new BorderStroke(Color.RED,
//                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        // inkwell
        if(hasInkwell) {
            ImageView inkwell = new ImageView(new Image("/assets/gui/playerboard/inkwell.png"));
            inkwell.setFitHeight(120);
            inkwell.setFitWidth(100);
            middleBox.getChildren().add(inkwell);
        }

        setBaseProduction(production, middleBox);
        setContainers(warehouse, strongbox);
        setDevSlotsBox();

        Group faithTrackGroup = new Group(faithTrack);
        board.add(faithTrackGroup, 1, 0);

        this.widthProperty().addListener((observable, oldValue, newValue) -> setSizes());
        this.heightProperty().addListener((observable, oldValue, newValue) -> setSizes());

    }

    private void setSizes() {
        double boardHeight = 720;
        double boardWidth = boardHeight * bgRatio;
        double storageColWidth = .2235 * boardWidth;

        if (getHeight() > 0 && getWidth() > 0) {
            double newDimRatio = getWidth() / getHeight();
            double newBoardHeight = newDimRatio > bgRatio ? getHeight() : getWidth() / bgRatio;
            double newBoardWidth = newDimRatio > bgRatio ? getHeight() * bgRatio : getWidth();
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

        double whWidth = warehouse.getWidth() > 0 ? warehouse.getWidth() : warehouse.getPrefWidth();
        double whScaleFactor = storageColWidth / whWidth;
        warehouse.setScaleX(whScaleFactor);
        warehouse.setScaleY(whScaleFactor);

        scalePreservingRatio(strongbox, storageColWidth, strongbox.getPrefWidth() / strongbox.getPrefHeight());

        for (DevSlot devSlot : devSlots)
            scalePreservingRatio(devSlot,
                    board.getColumnConstraints().get(5).getPercentWidth() * boardWidth / 100,
                    devSlot.getPrefWidth() / devSlot.getPrefHeight());

        double ftWidth = faithTrack.getWidth() > 0 ? faithTrack.getWidth() : 1768;
        double ftScaleFactor = boardWidth / ftWidth;
        faithTrack.setScaleX(ftScaleFactor);
        faithTrack.setScaleY(ftScaleFactor);
    }

    private static void scalePreservingRatio(Pane child, double parentSizeLimitWidth, double componentRatio) {
        child.setPrefWidth(parentSizeLimitWidth);
        child.setPrefHeight(parentSizeLimitWidth / componentRatio);
    }

    /**
     * Sets and displays a background for the playerboard.
     */
    private void setBackground() {
        frontBG.setImage(new Image(Objects.requireNonNull(getClass().getResource("/assets/gui/playerboard/background.png")).toExternalForm()));
    }

    /**
     * Updates a marker in the faith track.
     *
     * @param event     the event object containing the info
     * @param oldPoints    the faith points before update
     */
    public void updateFaithPoints(UpdateFaithPoints event, int oldPoints) {
        if (event.isBlackCross())
            faithTrack.updateBlackMarker(event.getFaithPoints(), oldPoints);
        else
            faithTrack.updatePlayerMarker(event.getPlayer(), event.getFaithPoints(), oldPoints);
    }

    /**
     * Adds the buttons to select the productions to be activated.
     */
    public void addProduceButtons() {
        devSlots.forEach(DevSlot::addProduceButton);
        production.addProduceButton();
    }

    public void setContainers(Warehouse warehouse, Strongbox strongbox) {
        this.warehouse = warehouse;
        this.strongbox = strongbox;
        storageColumn.getChildren().clear();
        storageColumn.add(new Group(warehouse), 0, 1);
        storageColumn.add(strongbox, 0, 3);
    }

    public void setBaseProduction(Production production, VBox middleBox) {
        this.production = production;

        StackPane baseProduction = new StackPane();
        baseProduction.setMaxHeight(107);
        baseProduction.setScaleX(0.95);
        baseProduction.setScaleY(0.95);

        ImageView baseProdPaper = new ImageView(new Image("/assets/gui/playerboard/baseproduction.png"));
        baseProdPaper.setFitWidth(107);
        baseProdPaper.setFitHeight(107);
        baseProduction.getChildren().add(baseProdPaper);

        production.setScaleX(0.85);
        production.setScaleY(0.85);
        StackPane.setAlignment(production, Pos.BOTTOM_LEFT);
        baseProduction.getChildren().add(production);

        middleBox.getChildren().add(baseProduction);
    }

    public void setDevSlotsBox() {
        HBox devSlotsBox = new HBox();
        devSlotsBox.setAlignment(Pos.TOP_CENTER);
        devSlots.forEach(devSlot -> devSlot.setMinWidth(197));
        devSlotsBox.getChildren().addAll(devSlots);
        board.add(devSlotsBox, 4, 1);
    }
}
