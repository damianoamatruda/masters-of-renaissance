package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.events.mvevents.UpdateFaithPoints;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * GUI component representing a player's playerboard.
 */
public class Playerboard extends HBox {
    private final VBox middleBox;
    private final FaithTrack faithTrack;
    private final double bgPixelWidth = 908;
    private final double bgPixelHeight = 646;
    private final double bgRatio = bgPixelWidth / bgPixelHeight;
    private final double storageColWidthPercentage = .2235;
    @FXML
    private ImageView frontBG;
    private List<DevSlot> devSlots;
    @FXML
    private GridPane board;
    @FXML
    private GridPane storageColumn;
    private Warehouse warehouse;
    private Strongbox strongbox;
    private Production production;
    private double _boardHeight;
    private double _boardWidth;
    private double _storageColWidth;

    /**
     * Class constructor.
     *
     * @param warehouse  the player's warehouse
     * @param strongbox  the player's strongbox
     * @param production the base production
     * @param devSlots   the player's development slots
     * @param faithTrack the faith track
     * @param hasInkwell true if player has inkwell
     */
    public Playerboard(Warehouse warehouse, Strongbox strongbox, Production production, List<DevSlot> devSlots, FaithTrack faithTrack, boolean hasInkwell) {
        this.faithTrack = faithTrack;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/playerboard.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        setBoardSizes();

        setBackground();

        middleBox = new VBox();
        middleBox.setAlignment(Pos.CENTER);
        middleBox.setSpacing(30);
        board.add(middleBox, 3, 1);

        /* Inkwell */
        if (hasInkwell) {
            ImageView inkwell = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/gui/images/playerboard/inkwell.png"))));
            inkwell.setPreserveRatio(true);
            inkwell.setFitHeight(90);
            middleBox.getChildren().add(inkwell);
        }

        setBaseProduction(production, middleBox);
        setContainers(warehouse, strongbox);
        setDevSlotsBox(devSlots);

        HBox ftContainer = new HBox(new Group(faithTrack));
        ftContainer.setAlignment(Pos.CENTER);

        board.add(ftContainer, 1, 0, 4, 1);

        this.widthProperty().addListener((observable, oldValue, newValue) -> setSizes());
        this.heightProperty().addListener((observable, oldValue, newValue) -> setSizes());
    }

    private static void scalePreservingRatio(Pane child, double parentSizeLimitWidth, double componentRatio) {
        child.setPrefWidth(parentSizeLimitWidth);
        child.setPrefHeight(parentSizeLimitWidth / componentRatio);
    }

    private void setBoardSizes() {
        _boardHeight = 720;
        _boardWidth = _boardHeight * bgRatio;
        _storageColWidth = storageColWidthPercentage * _boardWidth;

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
            _boardWidth = board.getWidth();
            _boardHeight = board.getHeight();
            _storageColWidth = storageColumn.getWidth();
        }
    }

    private void setContainersSize(double storageColWidth) {
        double whWidth = warehouse.getWidth() > 0 ? warehouse.getWidth() : warehouse.getPrefWidth();
        double whScaleFactor = storageColWidth / whWidth;
        warehouse.setScaleX(whScaleFactor);
        warehouse.setScaleY(whScaleFactor);

        scalePreservingRatio(strongbox, storageColWidth, strongbox.getPrefWidth() / strongbox.getPrefHeight());
    }

    private void setDevSlotsSize(double boardWidth) {
        for (DevSlot devSlot : devSlots)
            scalePreservingRatio(devSlot,
                    board.getColumnConstraints().get(5).getPercentWidth() * boardWidth / 100,
                    devSlot.getPrefWidth() / devSlot.getPrefHeight());
    }

    private void setFaithTrackSize(double boardWidth) {
        double ftWidth = faithTrack.getWidth() > 0 ? faithTrack.getWidth() : 1768;
        double ftScaleFactor = boardWidth / ftWidth;
        faithTrack.setScaleX(ftScaleFactor);
        faithTrack.setScaleY(ftScaleFactor);
    }

    private void setBaseProductionSize(double boardWidth) {
        double prodSizeRatio = .8 * (board.getColumnConstraints().get(3).getPercentWidth() * boardWidth / 100) / production.getMaxWidth();
        production.setScaleX(prodSizeRatio);
        production.setScaleY(prodSizeRatio);
    }

    private void setMiddleBoxSize(double boardWidth) {
        double midBoxWidth = board.getColumnConstraints().get(3).getPercentWidth() * boardWidth / 100;
        middleBox.setMaxWidth(midBoxWidth);
        middleBox.setMinWidth(midBoxWidth);
    }

    private void setSizes() {
        setBoardSizes();

        frontBG.setFitWidth(_boardWidth);
        frontBG.setFitHeight(_boardHeight);

        setContainersSize(_storageColWidth);

        setDevSlotsSize(_boardWidth);

        setFaithTrackSize(_boardWidth);

        setBaseProductionSize(_boardWidth);

        setMiddleBoxSize(_boardWidth);
    }

    /**
     * Sets and displays a background for the playerboard.
     */
    private void setBackground() {
        frontBG.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/gui/images/playerboard/background.png"))));
    }

    /**
     * Updates a marker in the faith track.
     *
     * @param event     the event object containing the info
     * @param oldPoints the faith points before update
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

        setContainersSize(_storageColWidth);
    }

    public void setBaseProduction(Production production, VBox middleBox) {
        this.production = production;

        StackPane baseProduction = new StackPane();
        baseProduction.setMaxHeight(100);
        baseProduction.setMaxWidth(100);

        ImageView baseProdPaper = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/gui/images/playerboard/baseproduction.png"))));
        baseProdPaper.setFitWidth(100);
        baseProdPaper.setFitHeight(100);
        baseProduction.getChildren().add(baseProdPaper);

        StackPane.setAlignment(production, Pos.BOTTOM_LEFT);
        baseProduction.getChildren().add(production);

        middleBox.getChildren().add(baseProduction);
    }

    public void setDevSlotsBox(List<DevSlot> devSlots) {
        this.devSlots = devSlots;
        HBox devSlotsBox = new HBox();
        devSlotsBox.setAlignment(Pos.TOP_CENTER);
        devSlots.forEach(devSlot -> devSlot.setMinWidth(197));
        devSlotsBox.getChildren().addAll(devSlots);
        board.add(devSlotsBox, 4, 1);

        setDevSlotsSize(_boardWidth);
    }
}
