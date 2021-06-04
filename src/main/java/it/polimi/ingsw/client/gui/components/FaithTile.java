package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.common.reducedmodel.ReducedYellowTile;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.Objects;

public class FaithTile extends StackPane {
    private int tileId;
    private ImageView bg;

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

        ImageView bg = new ImageView(bgimg);
        this.bg = bg;
        this.getChildren().add(bg);
        if(isSectionEnd) {
            ImageView v = new ImageView("/assets/gui/faithtrack/vaticanreport.png");
            v.setScaleX(bg.getScaleX() / 1.5);
            v.setScaleY(bg.getScaleY() / 1.5);
            this.getChildren().add(v);
        }

        if(isYellow) {
            ImageView bonusPts = new ImageView(new Image("/assets/gui/faithtrack/victorypointmark.png"));
            bonusPts.setScaleX(bg.getScaleX() / 2);
            bonusPts.setScaleY(bg.getScaleY() / 2);
            bonusPts.setLayoutY(bg.getScaleX() - bonusPts.getScaleY() / 2);
            this.getChildren().add(bonusPts);

            ReducedYellowTile y = Gui.getInstance().getViewModel().getFaithTrack().getYellowTiles().stream().filter(yt -> yt.getFaithPoints() == tileId).findAny().orElseThrow();
            Text t = new Text(y.getVictoryPoints()+"");
            t.setScaleX(bg.getScaleY()*2);
            t.setScaleY(bg.getScaleY()*2);
            this.getChildren().add(t);
        }

    }

//    public int getTileId() {
//        return tileId;
//    }

    public FaithTile() {
        this(0, false, false, false);
        this.setOpacity(0);
    }

    public void addPlayerMarker() {
        ImageView marker = new ImageView(new Image("/assets/gui/faithtrack/faithmarker.png"));
        marker.setScaleX(bg.getScaleX() / 1.5);
        marker.setScaleY(bg.getScaleY() / 1.5);
        this.getChildren().add(marker);
    }

    public void addBlackMarker() {
        ImageView marker = new ImageView(new Image("/assets/gui/faithtrack/blackcross.png"));
        marker.setScaleX(bg.getScaleX() / 1.2);
        marker.setScaleY(bg.getScaleY() / 1.2);
        this.getChildren().add(marker);
    }
}
