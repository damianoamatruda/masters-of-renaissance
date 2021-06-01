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
import javafx.scene.paint.Color;

public class Playerboard extends StackPane {
    @FXML private GridPane board;

    private StackPane canvas;
    private Warehouse w;

    private double bgPixelWidth = 908,
                   bgPixelHeight = 443,
                   bgRatio = bgPixelWidth / bgPixelHeight;
                //    warehousePixelSize = 140,
                //    baseProdPixelSize = 94,
                //    strongboxPixelWidth = 165,
                //    strongboxPixelHeight = 140,
                //    devSlotPixelWidth = 158,
                //    devSlotPixelHeight = 335;
        this.w = w;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/assets/gui/components/playerboard.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        board.setBorder(new Border(new BorderStroke(Color.RED,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        board.setGridLinesVisible(true);

        setBackground();

        board.add(w, 1, 0);

        canvas = this;
        this.widthProperty().addListener(this::changedSize);
        this.heightProperty().addListener(this::changedSize);
    }

    private static void setSize(Region r) {
        double scaleY = r.getWidth() / r.getPrefWidth();
        r.setScaleY(scaleY);
    }

    private <T> void changedSize(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        double newDimRatio = this.getWidth() / this.getHeight();

        board.setMaxHeight(newDimRatio > bgRatio ? this.getHeight() : this.getWidth() / (bgRatio));
        board.setMaxWidth(newDimRatio > bgRatio ? this.getHeight() * (bgRatio) :this.getWidth());

        setSize(w);
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
