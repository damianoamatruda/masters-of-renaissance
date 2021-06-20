package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.reducedmodel.ReducedFaithTrack;
import it.polimi.ingsw.common.reducedmodel.ReducedVaticanSection;
import it.polimi.ingsw.common.reducedmodel.ReducedYellowTile;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/** Gui component representing the faith track. */
public class FaithTrack extends HBox {
    Map<Integer, FaithTile> tiles = new HashMap<>();
    private VBox tilesBox = new VBox();
    private VBox popesFavors = new VBox();

    /**
     * Class constructor.
     *
     * @param track the cached faith track
     */
    public FaithTrack(ReducedFaithTrack track) {
        ViewModel vm = Gui.getInstance().getViewModel();

        List<ReducedYellowTile> yellowTiles = track.getYellowTiles();
        List<Integer> yellowTilesIndexes = yellowTiles.stream().map(ReducedYellowTile::getFaithPoints).toList();
        Map<Integer, ReducedVaticanSection> sections = track.getVaticanSections();
        List<Integer> sectionEnds = sections.keySet().stream().map(k -> sections.get(k).getFaithPointsEnd()).toList();
        List<Integer> sectionTiles = sections.keySet().stream().flatMapToInt(k -> IntStream.range(sections.get(k).getFaithPointsBeginning(), sections.get(k).getFaithPointsEnd()+1)).boxed().toList();

        this.setAlignment(Pos.CENTER);
        this.setSpacing(-165);
        this.setPadding(new Insets(100,100,0,100));

        HBox hBox = new HBox();
        hBox.setScaleX(new FaithTile().getScaleX() / 1.5);
        hBox.setScaleY(new FaithTile().getScaleX() / 1.5);
        for (int i = 0; i <= track.getMaxFaith(); i++) {
            if(i % 10 >= 3 && i % 10 <= 6) {
                FaithTile tile = new FaithTile(i, yellowTilesIndexes.contains(i), sectionTiles.contains(i), sectionEnds.contains(i));
                hBox.getChildren().add(tile);
                tiles.put(i, tile);
            }
            else if(i % 10 == 0 || i % 10 == 9) {
                hBox.getChildren().add(new FaithTile());
            }
        }
        tilesBox.getChildren().add(hBox);

        hBox = new HBox();
        hBox.setScaleX(new FaithTile().getScaleX() / 1.5);
        hBox.setScaleY(new FaithTile().getScaleX() / 1.5);
        for (int i = 0; i <= track.getMaxFaith(); i++) {
            if(i % 5 == 2) {
                FaithTile tile = new FaithTile(i, yellowTilesIndexes.contains(i), sectionTiles.contains(i), sectionEnds.contains(i));
                hBox.getChildren().add(tile);
                tiles.put(i, tile);
            }
            else if(i % 10 == 0 || i % 10 == 9 || i % 10 == 4 || i % 10 == 5) {
                hBox.getChildren().add(new FaithTile());
            }
        }
        tilesBox.getChildren().add(hBox);


        hBox = new HBox();
        hBox.setScaleX(new FaithTile().getScaleX() / 1.5);
        hBox.setScaleY(new FaithTile().getScaleX() / 1.5);
        for (int i = 0; i <= track.getMaxFaith(); i++) {
            if(i % 10 >= 8 || i % 10 <= 1) {
                FaithTile tile = new FaithTile(i, yellowTilesIndexes.contains(i), sectionTiles.contains(i), sectionEnds.contains(i));
                hBox.getChildren().add(tile);
                tiles.put(i, tile);
            }
            else if(i % 10 == 4 || i % 10 == 5) {
                hBox.getChildren().add(new FaithTile());
            }
        }
        tilesBox.getChildren().add(hBox);

        if(vm.getPlayerNicknames().size() == 1)
            updateBlackMarker(vm.getBlackCrossFP(), -1);

        updatePlayerMarker(vm.getPlayerFaithPoints(vm.getCurrentPlayer()), -1);
//        if(!vm.getLocalPlayerNickname().equals(vm.getCurrentPlayer()))
//            updatePlayerMarker(vm.getPlayerFaithPoints(vm.getLocalPlayerNickname()), -1);

        // display pope's favors
        int i = 1;
        for(ReducedVaticanSection section : sections.values()) {
            HBox hbox = new HBox();
            hbox.setAlignment(Pos.CENTER);
            hbox.setSpacing(50);

            Text sectionText = new Text("Section:" + i);
            sectionText.setScaleX(2);
            sectionText.setScaleY(2);
            hbox.getChildren().add(sectionText);

            boolean gotBonus = section.getBonusGivenPlayers().contains(vm.getCurrentPlayer());
            boolean isActivated = section.isActivated();
            StackPane favorPane = new StackPane();
            ImageView popesFavor = getPopesFavorImage(isActivated, gotBonus);
            popesFavor.setFitHeight(110);
            popesFavor.setFitWidth(110);
            favorPane.getChildren().add(popesFavor);
            if(gotBonus && isActivated) favorPane.getChildren().add(new Text(section.getVictoryPoints() + ""));

            hbox.getChildren().add(favorPane);
            popesFavors.getChildren().add(hbox);
            i++;
        }

        popesFavors.setSpacing(5);
        this.getChildren().add(popesFavors);
        tilesBox.setSpacing(-40);
        this.getChildren().add(tilesBox);

    }

    /**
     * Updates the position of the player's faith marker.
     *
     * @param faithPoints   the updated player's faith points
     * @param oldPts        the player's faith points before moving (used to remove marker from old tile)
     */
    public void updatePlayerMarker(int faithPoints, int oldPts) {
        if(oldPts >= 0) tiles.get(oldPts).removePlayerMarker();
        tiles.get(Integer.min(faithPoints, Gui.getInstance().getViewModel().getFaithTrack().orElseThrow().getMaxFaith())).addPlayerMarker();
    }

    /**
     * Updates the position of Lorenzo's faith marker.
     *
     * @param blackPoints   the updated Lorenzo's faith points
     * @param oldPts        Lorenzo's faith points before moving (used to remove marker from old tile)
     */
    public void updateBlackMarker(int blackPoints, int oldPts) {
        if(oldPts >= 0) tiles.get(oldPts).removeBlackMarker();
        tiles.get(Integer.min(blackPoints, Gui.getInstance().getViewModel().getFaithTrack().orElseThrow().getMaxFaith())).addBlackMarker();
    }

    public ImageView getPopesFavorImage(boolean isActivated, boolean gotBonus) {
        ImageView popesFavor;
        if(!isActivated) return new ImageView("/assets/gui/faithtrack/popesfavorinactive.png");
        if(gotBonus)
            popesFavor = new ImageView(new Image("/assets/gui/faithtrack/popesfavor.png"));
        else
            popesFavor = new ImageView(new Image("/assets/gui/faithtrack/popesfavormissed.png"));
        return popesFavor;
    }
}