package it.polimi.ingsw.client.gui.components;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class FaithTile extends ImageView {
    private int tileId;

    public FaithTile(int tileId, boolean isYellow, boolean isSectionEnd) {
        this.tileId = tileId;
        Image bgimg;

        if (isYellow) {
            bgimg = new Image(
                    Objects.requireNonNull(getClass().getResource("/assets/gui/faithtrack/yellowtile.png")).toExternalForm());
        } else
            bgimg = new Image(
                    Objects.requireNonNull(getClass().getResource("/assets/gui/faithtrack/faithtile.png")).toExternalForm());

        this.setImage(bgimg);

    }

    public FaithTile() {
        Image bgimg;
        bgimg = new Image(
                Objects.requireNonNull(getClass().getResource("/assets/gui/faithtrack/faithtile.png")).toExternalForm());

        this.setImage(bgimg);
        this.setOpacity(0);
    }
}
