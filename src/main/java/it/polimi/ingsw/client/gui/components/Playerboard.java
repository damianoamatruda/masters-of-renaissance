package it.polimi.ingsw.client.gui.components;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import it.polimi.ingsw.client.gui.Gui;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.Objects;

public class Playerboard extends StackPane {
    @FXML private StackPane canvas;
    @FXML private GridPane board;
    @FXML private GridPane storageColumn;
//    @FXML private Button left;
//    @FXML private Button right;

    private Warehouse w;
    private Strongbox s;
    private Production p;
    private List<DevSlot> slots;
    private FaithTrack faithTrack;

    private double bgPixelWidth = 908,
                   bgPixelHeight = 646,
                   bgRatio = bgPixelWidth / bgPixelHeight;

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
        
        // double fth = board.getRowConstraints().get(0).getPercentHeight() / 100 * board.getHeight();
        // double ftw = boardWidth;
        // faithTrack.setMinWidth(ftw);
        // faithTrack.setMaxWidth(ftw);
        // faithTrack.setMinHeight(fth);
        // faithTrack.setMaxHeight(fth);
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
        Image backBGImage = new Image(
            Objects.requireNonNull(getClass().getResource("/assets/gui/background.png")).toExternalForm());
        
        BackgroundImage frontBG = new BackgroundImage(frontBGImage,
            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
            new BackgroundSize(1.0, 1.0, true, true, true, false));
        BackgroundImage backBG = new BackgroundImage(backBGImage,
            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
            new BackgroundSize(1.0, 1.0, true, true, false, true));
        Background bg = new Background(backBG, frontBG);
        this.setBackground(bg);
    }

}
