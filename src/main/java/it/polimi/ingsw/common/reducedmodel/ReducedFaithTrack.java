package it.polimi.ingsw.common.reducedmodel;

import java.util.List;
import java.util.Map;

public class ReducedFaithTrack {
    private final Map<Integer, ReducedVaticanSection> vaticanSections;
    private final List<ReducedYellowTile> yellowTiles;

    /**
     * @param vaticanSections
     * @param yellowTiles
     */
    public ReducedFaithTrack(Map<Integer, ReducedVaticanSection> vaticanSections, List<ReducedYellowTile> yellowTiles) {
        this.vaticanSections = vaticanSections;
        this.yellowTiles = yellowTiles;
    }

    /**
     * @return the yellowTiles
     */
    public List<ReducedYellowTile> getYellowTiles() {
        return yellowTiles;
    }

    /**
     * @return the vaticanSections
     */
    public Map<Integer, ReducedVaticanSection> getVaticanSections() {
        return vaticanSections;
    }
}
