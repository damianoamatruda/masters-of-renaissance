package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.common.reducedmodel.ReducedFaithTrack;
import it.polimi.ingsw.common.reducedmodel.ReducedVaticanSection;
import it.polimi.ingsw.common.reducedmodel.ReducedYellowTile;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class FaithTrack extends VBox {
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
                hBox.getChildren().add(new FaithTile(2, yellowTilesIndexes.contains(i), sectionTiles.contains(i), sectionEnds.contains(i)));
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
                hBox.getChildren().add(new FaithTile(2, yellowTilesIndexes.contains(i), sectionTiles.contains(i), sectionEnds.contains(i)));
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
                  hBox.getChildren().add(new FaithTile(2, yellowTilesIndexes.contains(i), sectionTiles.contains(i), sectionEnds.contains(i)));
            }
            else if(i % 10 == 4 || i % 10 == 5) {
                hBox.getChildren().add(new FaithTile());
            }
        }
        this.getChildren().add(hBox);
    }
}