package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.client.viewmodel.ViewModel;
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
        ViewModel vm = Gui.getInstance().getViewModel();

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

        if(vm.getPlayerNicknames().size() == 1)
            updateBlackMarker(vm.getBlackCrossFP(), -1);

        updatePlayerMarker(vm.getPlayerFaithPoints(vm.getCurrentPlayer()), -1);
//        if(!vm.getLocalPlayerNickname().equals(vm.getCurrentPlayer()))
//            updatePlayerMarker(vm.getPlayerFaithPoints(vm.getLocalPlayerNickname()), -1);
    }

    public void updatePlayerMarker(int faithPoints, int oldPts) {
        if(oldPts >= 0) tiles.get(oldPts).removePlayerMarker();
        tiles.get(Integer.min(faithPoints, Gui.getInstance().getViewModel().getFaithTrack().orElseThrow().getMaxFaith())).addPlayerMarker();
    }

    public void updateBlackMarker(int blackPoints, int oldPts) {
        if(oldPts >= 0) tiles.get(oldPts).removeBlackMarker();
        tiles.get(Integer.min(blackPoints, Gui.getInstance().getViewModel().getFaithTrack().orElseThrow().getMaxFaith())).addBlackMarker();
    }
}