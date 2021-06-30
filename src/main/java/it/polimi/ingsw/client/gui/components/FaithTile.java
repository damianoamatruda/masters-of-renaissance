package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.ViewModel;
import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.common.reducedmodel.ReducedYellowTile;
import javafx.scene.CacheHint;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Objects;

/** Gui component representing a single Faith Track tile. */
public class FaithTile extends StackPane {
    private final int tileId;
    private final ImageView bg;
    private final Pane markersPane;

    /**
     * Class constructor.
     *
     * @param tileId       progressive number of tile
     * @param isYellow     true if it is a milestone to higher bonus points
     * @param isSection    true if included inside of a Vatican Section
     * @param isSectionEnd true if Vatican Report Tile
     */
    public FaithTile(int tileId, boolean isYellow, boolean isSection, boolean isSectionEnd) {
        this.tileId = tileId;
        String template;
        markersPane = new Pane();
        markersPane.setMaxHeight(117);
        markersPane.setMaxWidth(117);

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
        if (isSectionEnd) {
            ImageView v = new ImageView("/assets/gui/faithtrack/vaticanreport.png");
            v.setScaleX(bg.getScaleX() / 1.5);
            v.setScaleY(bg.getScaleY() / 1.5);
            this.getChildren().add(v);
        }

        if (isYellow) {
            ImageView bonusPts = new ImageView(new Image("/assets/gui/faithtrack/victorypointmark.png"));
            bonusPts.setScaleX(bg.getScaleX() / 2);
            bonusPts.setScaleY(bg.getScaleY() / 2);
            bonusPts.setLayoutY(bg.getScaleX() - bonusPts.getScaleY() / 2);
            this.getChildren().add(bonusPts);

            ReducedYellowTile y = Gui.getInstance().getViewModel().getFaithTrack().orElseThrow().getYellowTiles().stream().filter(yt -> yt.getFaithPoints() == tileId).findAny().orElseThrow();
            Text t = new Text(String.valueOf(y.getVictoryPoints()));
            t.setScaleX(bg.getScaleY() * 2);
            t.setScaleY(bg.getScaleY() * 2);
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
     *
     * @param player the player represented by the marker
     * @param markers
     */
    public void addPlayerMarker(String player, HashMap<String, ImageView> markers) {
        ImageView marker = new ImageView(new Image("/assets/gui/faithtrack/faithmarker.png"));

        setMarkerColor(marker, player);

        marker.setScaleX(bg.getScaleX() / 1.5);
        marker.setScaleY(bg.getScaleY() / 1.5);

        if(!this.getChildren().contains(markersPane))
            this.getChildren().add(markersPane);

        markersPane.getChildren().add(marker);

        adjustMarkers();
        markers.put(player, marker);
    }

    /**
     * Sets a custom color to the player's faith marker.
     *
     * @param marker the player's faith marker
     * @param player the player from which color will be determined
     */
    private void setMarkerColor(ImageView marker, String player) {
        ViewModel vm = Gui.getInstance().getViewModel();

        vm.getHexPlayerColor(player).ifPresent(hex -> {
            marker.setClip(new ImageView(marker.getImage()));

            ColorAdjust monochrome = new ColorAdjust();
            monochrome.setSaturation(-1.0);

            Blend colorChange = new Blend(
                    BlendMode.MULTIPLY,
                    monochrome,
                    new ColorInput(
                            0,
                            0,
                            marker.getImage().getWidth(),
                            marker.getImage().getHeight(),
                            Color.web(hex)
                    )
            );

            marker.setEffect(colorChange);

            marker.setCache(true);
            marker.setCacheHint(CacheHint.SPEED);
        });
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
     *
     * @param player
     * @param markers
     */
    public void removePlayerMarker(String player, HashMap<String, ImageView> markers) {
        markersPane.getChildren().remove(markers.get(player));
        markers.remove(player);
        adjustMarkers();
    }

    /**
     * Adjusts positions of overlapping markers
     */
    private void adjustMarkers() {
        if(markersPane.getChildren().size() > 0)
            markersPane.getChildren().get(0).setLayoutX(12);

        // if overlapping player markers
        if (markersPane.getChildren().stream().filter(img -> img instanceof ImageView && ((ImageView) img).getImage().getUrl().contains("faithmarker")).count() >= 2) {
            for (int i = 1; i < markersPane.getChildren().size(); i++) {
                markersPane.getChildren().subList(0, i).forEach(m -> m.setLayoutX(m.getLayoutX() - 7));
                markersPane.getChildren().get(i).setLayoutX(markersPane.getChildren().get(i - 1).getLayoutX() + 10);
            }
        }
    }

    /**
     * Removes Lorenzo's faith marker (used when it has to be updated to a new position).
     */
    public void removeBlackMarker() {
        this.getChildren().removeIf(img -> img instanceof ImageView && ((ImageView) img).getImage().getUrl().contains("blackcross"));
    }
}
