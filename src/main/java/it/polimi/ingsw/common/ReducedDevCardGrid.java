package it.polimi.ingsw.common;

import it.polimi.ingsw.server.model.DevCardGrid;
import it.polimi.ingsw.server.model.DevelopmentCard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ReducedDevCardGrid {
    /** Number of rows of separate decks that represent different development card levels. */
    protected final int levelsCount;

    /** Number of columns of separate decks that represent different development card colors. */
    protected final int colorsCount;

    /** All the cards that are still not bought by any player. */
    protected final Map<String, List<Stack<Integer>>> grid;

    public ReducedDevCardGrid(DevCardGrid grid) {
        this.levelsCount = grid.getLevelsCount();
        this.colorsCount = grid.getColorsCount();
        this.grid = new HashMap<>();
        grid.getGrid().forEach((key, value) -> this.grid.put(key.getName(), value.stream().map(this::reduceDeck).toList()));
    }

    private Stack<Integer> reduceDeck (Stack<DevelopmentCard> s) {
        Stack<Integer> reducedDeck = new Stack<>();
        for(int i = 0; i < s.size(); i++) {
            reducedDeck.push(s.get(i).getId());
        }
        return reducedDeck;
    }
}
