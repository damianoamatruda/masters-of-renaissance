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

public class Playerboard extends HBox {
    @FXML private HBox canvas;
    @FXML private ImageView frontBG;
    @FXML private GridPane board;
    @FXML private GridPane storageColumn;
    @FXML private VBox leadersBox;
//    @FXML private Button left;
//    @FXML private Button right;

    private final Warehouse w;
    private final Strongbox s;
    private final Production p;
    private final List<DevSlot> slots;
    private final FaithTrack faithTrack;
    private final List<LeaderCard> leaders;

    private final double bgPixelWidth = 908;
    private final double bgPixelHeight = 646;
    private final double bgRatio = bgPixelWidth / bgPixelHeight;

    public Playerboard(Warehouse w, Strongbox s, Production p, List<DevSlot> slots, FaithTrack faithTrack, List<LeaderCard> leaders) {
        this.w = w;
        this.s = s;
        this.p = p;
        this.slots = slots;
        this.faithTrack = faithTrack;
        this.leaders = leaders;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/playerboard.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        board.setGridLinesVisible(true);
        board.setBorder(new Border(new BorderStroke(Color.RED,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        
        storageColumn.setGridLinesVisible(true);
        storageColumn.setBorder(new Border(new BorderStroke(Color.GREEN,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        w.setBorder(new Border(new BorderStroke(Color.BLUE,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        s.setBorder(new Border(new BorderStroke(Color.BLUE,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        p.setBorder(new Border(new BorderStroke(Color.BLUE,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        slots.forEach(sl -> sl.setBorder(new Border(new BorderStroke(Color.BLUE,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT))));
        faithTrack.setBorder(new Border(new BorderStroke(Color.BLUE,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        leadersBox.setBorder(new Border(new BorderStroke(Color.RED,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        setBackground();

        storageColumn.add(w, 0, 1);
        storageColumn.add(s, 0, 3);

        board.add(p, 3, 1);

        for(int i = 0; i < slots.size(); i++){
            slots.get(i).setAlignment(Pos.BOTTOM_CENTER);
            board.add(slots.get(i), 4 + i, 1);
        }

        Group g = new Group(faithTrack);
        // faithTrack.setLayoutX(-250);
        // g.setAutoSizeChildren(true);
        // g.setScaleX(g.getScaleX() * 0.85);
        // g.setScaleY(g.getScaleY() * 0.85);
        board.add(g, 1, 0);

        leadersBox.getChildren().addAll(leaders);

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

        scalePreservingRatio(w, storageColWidth, w.getPrefWidth() / w.getPrefHeight());
        
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
}
