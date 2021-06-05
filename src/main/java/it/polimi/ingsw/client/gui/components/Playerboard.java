package it.polimi.ingsw.client.gui.components;

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

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.common.events.mvevents.UpdateFaithPoints;

public class Playerboard extends HBox {
    @FXML private HBox canvas;
    @FXML private ImageView frontBG;
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

    public Playerboard(Warehouse w, Strongbox s, Production p, List<DevSlot> slots, FaithTrack faithTrack) {
        this.w = w;
        this.s = s;
        this.p = p;
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

        Group wh = new Group(w);

        storageColumn.add(wh, 0, 1);
        storageColumn.add(s, 0, 3);

        board.add(p, 3, 1);

        for(int i = 0; i < slots.size(); i++){
            slots.get(i).setAlignment(Pos.BOTTOM_CENTER);
            board.add(slots.get(i), 4 + i, 1);
        }

        Group g = new Group(faithTrack);
        g.setScaleX(g.getScaleX() * 1.3);
        g.setScaleY(g.getScaleY() * 1.3);
        board.add(g, 1, 0);


        changedSize(null, 0, 0);

        canvas = this;
        this.widthProperty().addListener(this::changedSize);
        this.heightProperty().addListener(this::changedSize);
    }

    private <T> void changedSize(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        double boardWidth = 1050, boardHeight = 745,
               storageColWidth = 232;

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

    private static void scalePreservingRatio(Pane child, double parentSizeLimitWidth, double componentRatio) {
        child.setPrefWidth(parentSizeLimitWidth);
        child.setPrefHeight(parentSizeLimitWidth / componentRatio);
    }

    private void setBackground() {
        Image frontBGImage = new Image(
                Objects.requireNonNull(getClass().getResource("/assets/gui/playerboard/background.png")).toExternalForm());

        frontBG.setImage(frontBGImage);
    }

    public void updateFaithPoints(UpdateFaithPoints event) {
        if (event.isBlackCross())
            faithTrack.updateBlackMarker(event.getFaithPoints());
        else if (Gui.getInstance().getViewModel().getLocalPlayerNickname().equals(event.getPlayer()))
            faithTrack.updatePlayerMarker(event.getFaithPoints());
    }
}
