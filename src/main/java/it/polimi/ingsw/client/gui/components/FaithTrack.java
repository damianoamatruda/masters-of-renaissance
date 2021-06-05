package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.common.reducedmodel.ReducedFaithTrack;
import it.polimi.ingsw.common.reducedmodel.ReducedVaticanSection;
import it.polimi.ingsw.common.reducedmodel.ReducedYellowTile;
import javafx.scene.layout.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class FaithTrack extends VBox {
    Map<Integer, FaithTile> tiles = new HashMap<>();

    public FaithTrack(ReducedFaithTrack track) {
        List<ReducedYellowTile> yellowTiles = track.getYellowTiles();
        List<Integer> yellowTilesIndexes = yellowTiles.stream().map(ReducedYellowTile::getFaithPoints).toList();
        Map<Integer, ReducedVaticanSection> sections = track.getVaticanSections();
        List<Integer> sectionEnds = sections.keySet().stream().map(k -> sections.get(k).getFaithPointsEnd()).toList();
        List<Integer> sectionTiles = sections.keySet().stream().flatMapToInt(k -> IntStream.range(sections.get(k).getFaithPointsBeginning(), sections.get(k).getFaithPointsEnd()+1)).boxed().toList();

        this.setSpacing(-40);
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
        this.getChildren().add(hBox);

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
        this.getChildren().add(hBox);


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
        this.getChildren().add(hBox);

        if(Gui.getInstance().getViewModel().getPlayerNicknames().size() == 1)
            updateBlackMarker(Gui.getInstance().getViewModel().getBlackCrossFP(), -1);
        updatePlayerMarker(Gui.getInstance().getViewModel().getPlayerData(Gui.getInstance().getViewModel().getLocalPlayerNickname()).getFaithPoints(), -1);
    }

    public void updatePlayerMarker(int faithPoints, int oldPts) {
        if(oldPts >= 0) tiles.get(oldPts).removePlayerMarker();
        tiles.get(faithPoints).addPlayerMarker();
    }

    public void updateBlackMarker(int blackPoints, int oldPts) {
        if(oldPts >= 0) tiles.get(oldPts).removeBlackMarker();
        tiles.get(blackPoints).addBlackMarker();
    }
}