package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.common.reducedmodel.ReducedYellowTile;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.Objects;

/** Gui component representing a single Faith Track tile. */
public class FaithTile extends StackPane {
    private int tileId;
    private ImageView bg;

    /**
     * Class constructor.
     *
     * @param tileId        progressive number of tile
     * @param isYellow      true if it is a milestone to higher bonus points
     * @param isSection     true if included inside of a Vatican Section
     * @param isSectionEnd  true if Vatican Report Tile
     */
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

            ReducedYellowTile y = Gui.getInstance().getViewModel().getFaithTrack().orElseThrow().getYellowTiles().stream().filter(yt -> yt.getFaithPoints() == tileId).findAny().orElseThrow();
            Text t = new Text(y.getVictoryPoints()+"");
            t.setScaleX(bg.getScaleY()*2);
            t.setScaleY(bg.getScaleY()*2);
            this.getChildren().add(t);
        }

    }

//    public int getTileId() {
//        return tileId;
//    }

    /**
     * Invisible tile, used only for alignment between other faith track tiles.
     */
    public FaithTile() {
        this(0, false, false, false);
        this.setOpacity(0);
    }

    /**
     * Sets and displays the player's faith marker.
     */
    public void addPlayerMarker() {
        ImageView marker = new ImageView(new Image("/assets/gui/faithtrack/faithmarker.png"));
        marker.setScaleX(bg.getScaleX() / 1.5);
        marker.setScaleY(bg.getScaleY() / 1.5);
        this.getChildren().add(marker);
    }

    /**
     * Sets and displays Lorenzo's faith marker.
     */
    public void addBlackMarker() {
        ImageView marker = new ImageView(new Image("/assets/gui/faithtrack/blackcross.png"));
        marker.setScaleX(bg.getScaleX() / 1.2);
        marker.setScaleY(bg.getScaleY() / 1.2);
        this.getChildren().add(marker);
    }

    /**
     * Removes the player's faith marker (used when it has to be updated to a new position).
     */
    public void removePlayerMarker() {
        this.getChildren().removeIf(img -> img instanceof ImageView && ((ImageView) img).getImage().getUrl().contains("faithmarker"));
    }

    /**
     * Removes Lorenzo's faith marker (used when it has to be updated to a new position).
     */
    public void removeBlackMarker() {
        this.getChildren().removeIf(img -> img instanceof ImageView && ((ImageView) img).getImage().getUrl().contains("blackcross"));
    }
}
