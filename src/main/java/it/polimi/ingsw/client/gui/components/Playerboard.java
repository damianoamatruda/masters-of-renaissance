package it.polimi.ingsw.client.gui.components;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.Objects;

public class Playerboard extends StackPane {
    @FXML
    private final StackPane canvas;
    @FXML
    private GridPane board;
    @FXML
    private GridPane storageColumn;
    private final Warehouse w;
    private final Strongbox s;
    private final Production p;
    private final DevSlot slot;

    private final double bgPixelWidth = 908;
    private final double bgPixelHeight = 443;
    private final double bgRatio = bgPixelWidth / bgPixelHeight;
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

    private <T> void changedSize(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        if (this.getHeight() > 0 || this.getWidth() > 0) {
            double newDimRatio = this.getWidth() / this.getHeight();
            board.setMaxHeight(newDimRatio > bgRatio ? this.getHeight() : this.getWidth() / (bgRatio));
            board.setMaxWidth(newDimRatio > bgRatio ? this.getHeight() * (bgRatio) : this.getWidth());
            
            scalePreservingRatio(w, storageColumn.getWidth(), w.getPrefWidth());
            scalePreservingRatio(s, storageColumn.getWidth(), s.getPrefWidth());
            
            scalePreservingRatio(p, board.getColumnConstraints().get(3).getPercentWidth() * board.getWidth() / 100, p.getMaxWidth());
            scalePreservingRatio(slot, board.getColumnConstraints().get(5).getPercentWidth() * board.getWidth() / 100, slot.getPrefWidth());
        }
    }

    private static void scalePreservingRatio(Pane child,
        double parentSizeLimit,
        double childCorrespondingSize) {
            
            double scaleRatio = parentSizeLimit / childCorrespondingSize;
            child.setScaleX(scaleRatio);
            child.setScaleY(scaleRatio);
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
