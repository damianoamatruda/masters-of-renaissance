package it.polimi.ingsw.common.reducedmodel;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReducedDevCardGrid {
    /** Number of rows of separate decks that represent different development card levels. */
    private final int levelsCount;

    /** Number of columns of separate decks that represent different development card colors. */
    private final int colorsCount;

    /** Topmost cards that are still not bought by any player. */
    private final Map<String, List<Integer>> topCards;

    /**
     * @param levelsCount the number of card levels
     * @param colorsCount the development cards' colors
     * @param topCards    the topmost visible cards
     */
    public ReducedDevCardGrid(int levelsCount, int colorsCount, Map<String, List<Integer>> topCards) {
        if (topCards == null)
            throw new IllegalArgumentException("Null card grid constructing reduced dev card grid.");
        
        this.levelsCount = levelsCount;
        this.colorsCount = colorsCount;
        this.topCards = topCards;
    }

    /**
     * @return the number of rows of separate decks that represent different development card levels
     */
    public int getLevelsCount() {
        return levelsCount;
    }

    /**
     * @return the number of columns of separate decks that represent different development card colors
     */
    public int getColorsCount() {
        return colorsCount;
    }

    /**
     * @return all the cards that are still not bought by any player
     */
    public Map<String, List<Optional<Integer>>> getTopCards() {
        return topCards.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, e -> e.getValue().stream().map(Optional::ofNullable).toList()));
    }
}
