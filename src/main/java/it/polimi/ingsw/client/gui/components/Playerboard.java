package it.polimi.ingsw.client.gui.components;

import java.io.IOException;
import java.util.Objects;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import javafx.scene.paint.Color;

public class Playerboard extends StackPane {
    @FXML private StackPane canvas;
    @FXML private GridPane board;
    @FXML private GridPane storageColumn;
    
    private Warehouse w;
    private Strongbox s;
    private Production p;
    private DevSlot slot;

    private double bgPixelWidth = 908,
                   bgPixelHeight = 443,
                   bgRatio = bgPixelWidth / bgPixelHeight;
                //    warehousePixelSize = 140,
                //    baseProdPixelSize = 94,
                //    strongboxPixelWidth = 165,
                //    strongboxPixelHeight = 140,
                //    devSlotPixelWidth = 158,
                //    devSlotPixelHeight = 335;

    public Playerboard(Warehouse w, Strongbox s, Production p, DevSlot slot) {
        this.w = w;
        this.s = s;
        this.p = p;
        this.slot = slot;

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
        slot.setBorder(new Border(new BorderStroke(Color.BLUE,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        setBackground();

        storageColumn.add(w, 0, 1);
        storageColumn.add(s, 0, 3);

        board.add(p, 3, 0);
        board.add(slot, 5, 0);

        canvas = this;
        this.widthProperty().addListener(this::changedSize);
        this.heightProperty().addListener(this::changedSize);
    }

    private static void setSize(GridPane parent, Region child, double parentWidth, boolean isStorage) {
        Integer colIndex = GridPane.getColumnIndex(child);

        if (isStorage)
            colIndex = 1;

        double colPercentWidth = parent.getColumnConstraints().get(colIndex).getPercentWidth(),
               colWidth = parentWidth * colPercentWidth / 100,
               scaleX = colWidth / child.getWidth(),
               scaleY = colWidth / child.getPrefWidth();
        child.setScaleX(scaleX > 0 ? scaleX : 1);
        child.setScaleY(scaleY > 0 ? scaleY : 1);
    }

    private <T> void changedSize(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        double newDimRatio = this.getWidth() / this.getHeight();

        if (this.getHeight() <= 0 || this.getWidth() <= 0) {
            board.setMaxHeight(canvas.getPrefHeight());
            board.setMaxWidth(canvas.getPrefWidth());
        } else {
            board.setMaxHeight(newDimRatio > bgRatio ? this.getHeight() : this.getWidth() / (bgRatio));
            board.setMaxWidth(newDimRatio > bgRatio ? this.getHeight() * (bgRatio) : this.getWidth());
        }

        setSize(board, w, board.getMaxWidth(), true);
        setSize(board, s, board.getMaxWidth(), true);
        setSize(board, p, board.getMaxWidth(), false);
        setSize(board, slot, board.getMaxWidth(), false);
    }

    private void setBackground() {
        Image frontBGImage = new Image(
            Objects.requireNonNull(getClass().getResource("/assets/gui/playerboard/background_no_faith_track.png")).toExternalForm());
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
