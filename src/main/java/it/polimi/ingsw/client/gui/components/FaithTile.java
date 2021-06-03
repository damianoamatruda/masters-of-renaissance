package it.polimi.ingsw.client.gui.components;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.Objects;

public class FaithTile extends StackPane {
    private int tileId;

    public FaithTile(int tileId, boolean isYellow, boolean isSection, boolean isSectionEnd) {
        this.tileId = tileId;
        String template;

        if (isYellow) {
            if(isSection) template = "/assets/gui/faithtrack/yellowsectiontile.png";
            else template = "/assets/gui/faithtrack/yellowtile.png";
        } else
            if(isSection) template = "/assets/gui/faithtrack/sectiontile.png";
            else template = "/assets/gui/faithtrack/faithtile.png";

        Image bgimg = new Image(Objects.requireNonNull(getClass().getResource(template)).toExternalForm());
//        BackgroundImage i = new BackgroundImage(bgimg,
//                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
//                new BackgroundSize(1.0, 1.0, true, true, true, false));

        ImageView bg = new ImageView(bgimg);
        this.getChildren().add(bg);
        if(isSectionEnd) {
            ImageView v = new ImageView("/assets/gui/faithtrack/vaticanreport.png");
            v.setScaleX(bg.getScaleX() / 1.5);
            v.setScaleY(bg.getScaleY() / 1.5);
            this.getChildren().add(v);
        }

    }

    public int getTileId() {
        return tileId;
    }

    public FaithTile() {
        this(0, false, false, false);
        this.setOpacity(0);
    }
}
