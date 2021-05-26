package it.polimi.ingsw.common.reducedmodel;

import java.util.List;
import java.util.Map;

public class ReducedFaithTrack {
    private final Map<Integer, ReducedVaticanSection> vaticanSections;
    private final List<ReducedYellowTile> yellowTiles;
    private final int maxFaith;

    /**
     * @param vaticanSections
     * @param yellowTiles
     */
    public ReducedFaithTrack(Map<Integer, ReducedVaticanSection> vaticanSections, List<ReducedYellowTile> yellowTiles, int maxFaith) {
        this.vaticanSections = vaticanSections;
        this.yellowTiles = yellowTiles;
        this.maxFaith = maxFaith;
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

    public int getMaxFaith() {
        return maxFaith;
    }
}
