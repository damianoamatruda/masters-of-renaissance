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
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class Playerboard extends StackPane {
    @FXML private AnchorPane board;

    private StackPane canvas;
    private Warehouse w;

    private double bgPixelWidth = 908,
                   bgPixelHeight = 443,
                   warehousePixelSize = 140,
                   baseProdPixelSize = 94,
                   strongboxPixelWidth = 165,
                   strongboxPixelHeight = 140,
                   devSlotPixelWidth = 158,
                   devSlotPixelHeight = 335;

    public Playerboard(Warehouse w) {
        this.w = w;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/playerboard.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        // w.setBorder(new Border(new BorderStroke(Color.RED,
        //     BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        setBackground();

        board.getChildren().add(w);

        canvas = this;
        // this.widthProperty().addListener((observable, oldValue, newValue) -> {
        //     setPositionAndSize(board, w, 0.04, 0.145, 0.176, 0.361);
        // });
        // this.heightProperty().addListener((observable, oldValue, newValue) -> {
        //     setPositionAndSize(board, w, 0.04, 0.145, 0.176, 0.361);
        // });
    }

    /**
     * Sets the position and scaling of the child node
     * given the parameters relative to the parent.
     * 
     * @param parent                    the parent AnchorPane
     * @param n                         the child
     * @param leftMarginPercent         the margin from the left border of the parent
     *                                  to the left border of the child, expressed as the
     *                                  percentage (ratio) of the width of the parent used by the margin
     * @param topMarginPercent          the margin from the top border of the parent
     *                                  to the top border of the child, expressed as the
     *                                  percentage (ratio) of the height of the parent used by the margin
     * @param childWidthParentPercent   the width of the child, expressed as
     *                                  the percentage of the width of the parent that the child should take
     * @param childHeightParentPercent  the height of the child, expressed as
     *                                  the percentage of the height of the parent that the child should take
     */
    private static void setPositionAndSize(AnchorPane parent, Region n,
        double leftMarginPercent, double topMarginPercent,
        double childWidthParentPercent, double childHeightParentPercent) {

        AnchorPane.setLeftAnchor(n, parent.getWidth() * leftMarginPercent);
        AnchorPane.setTopAnchor(n, parent.getHeight() * topMarginPercent);
    
        double scalex = childWidthParentPercent * parent.getWidth() / n.getPrefWidth(),
               scaley = childHeightParentPercent * parent.getHeight() / n.getPrefHeight();
        n.setScaleX(scalex);
        n.setScaleX(scaley);
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
