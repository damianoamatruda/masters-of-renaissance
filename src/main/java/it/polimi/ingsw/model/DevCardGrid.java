package it.polimi.ingsw.model;

import it.polimi.ingsw.model.devcardcolors.DevCardColor;
import it.polimi.ingsw.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.model.resourcetypes.ResourceType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents a grid of decks of development cards.
 */
public class DevCardGrid {
    /** Number of rows of separate decks that represent different development card levels. */
    protected final int levelsCount;

    /** Number of columns of separate decks that represent different development card colors. */
    protected final int colorsCount;

    /** All the cards that are still not bought by any player. */
    protected Map<DevCardColor, List<Stack<DevelopmentCard>>> grid;

    /**
     * Generates the grid.
     *
     * @param devCards      the list of all the development cards
     * @param levelsCount   the number of rows of separate decks that represent different development card levels
     * @param colorsCount   the number of columns of separate decks that represent different development card colors.
     */
    public DevCardGrid(List<DevelopmentCard> devCards, int levelsCount, int colorsCount) {
        this.grid = new HashMap<>();
        this.levelsCount = levelsCount;
        this.colorsCount = colorsCount;

        if (!devCards.isEmpty() && colorsCount != 0){
            for (DevelopmentCard card : devCards) {
                if (!grid.keySet().contains(card.getColor()))
                    grid.put(card.getColor(), new ArrayList<>() {{
                        add(0,null);
                        for (int i = 1; i <= levelsCount; i++)
                            add(i, new Stack<>());
                    }});

                List<Stack<DevelopmentCard>> column = grid.get(card.getColor());
                Stack<DevelopmentCard> deck = column.get(card.getLevel());
                deck.push(card);
            }
            for (DevCardColor column : grid.keySet()) {
                for (int cardLevel = 1; cardLevel <= levelsCount; cardLevel++)
                    Collections.shuffle(grid.get(column).get(cardLevel));
            }
        }
    }

    /**
     * Getter of the number of rows containing cards of different levels (from 1 to the max level).
     *
     * @return the maximum level of a card (= the number of rows)
     */
    public int getLevelsCount() {
        return levelsCount;
    }

    /**
     * Getter of the number of different colors a card can have (= columns of the set of buyable cards).
     *
     * @return the number of different card colors
     */
    public int getColorsCount() {
        return colorsCount;
    }

    /**
     * Retrieves the top card of each deck (the cards that can be bought during this turn).
     *
     * @return  the top card of each deck
     */
    public List<List<DevelopmentCard>> peekDevCards() {
        List<List<DevelopmentCard>> top = new ArrayList<>();
        for(DevCardColor color : grid.keySet()){
            top.add(grid.get(color)
                    .stream()
                    .map(deck -> deck.peek())
                    .collect(Collectors.toList()));
        }
        return top;
    }

    /**
     * A player buys a card of a given color and level.
     *
     * @param game                 the game the player is playing in
     * @param player               the player that wants to buy a card
     * @param color                the color of the card to be bought
     * @param level                the level of the card to be bought
     * @param position             the position of the dev slot where to put the development card
     * @param resContainers        a map of the resource containers where to take the storable resources
     * @throws Exception           Bought card cannot fit in chosen player slot
     * @throws Exception           error while player was depositing the card
     * @throws EmptyStackException No cards available with given color and level
     */
    public void buyDevCard(Game game, Player player, DevCardColor color, int level, int position,
                           Map<ResourceContainer, Map<ResourceType, Integer>> resContainers)
            throws Exception, EmptyStackException {

        DevelopmentCard card = grid.get(color).get(level).pop();
        try {
            boolean maxCardsReached = player.addToDevSlot(game, position, card, resContainers);
            game.onAddToDevSlot(player, maxCardsReached);
        }
        catch (Exception e){
            grid.get(color).get(level).push(card);
            throw new Exception();
        }
    }

    /**
     * Removes development cards, so that nobody else can purchase them.
     *
     * @param color     the color to be discarded
     * @param quantity  the number of cards to be discarded
     */
    public void discardDevCards(DevCardColor color, int quantity) {
        // TODO: Re-implement (this is an endless loop)
        int level = 1;
        while(quantity > 0 && level <= 3) {
            DevelopmentCard card = grid.get(color).get(level).pop();
        }
    }
}
