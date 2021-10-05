package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.EventDispatcher;
import it.polimi.ingsw.common.backend.model.resourcecontainers.IllegalResourceTransferException;
import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.common.backend.model.resourcetransactions.IllegalResourceTransactionContainersException;
import it.polimi.ingsw.common.backend.model.resourcetransactions.IllegalResourceTransactionReplacementsException;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.events.mvevents.UpdateDevCardGrid;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardGrid;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class represents a grid of decks of development cards.
 */
public class DevCardGrid extends EventDispatcher {
    /** Number of rows of separate decks that represent different development card levels. */
    private final int levelsCount;

    /** Number of columns of separate decks that represent different development card colors. */
    private final int colorsCount;

    /** All the cards that are still not bought by any player. */
    private final Map<DevCardColor, List<Stack<DevelopmentCard>>> grid;

    /**
     * Generates the grid.
     *
     * @param devCards    the list of all the development cards
     * @param levelsCount the number of rows of separate decks that represent different development card levels
     * @param colorsCount the number of columns of separate decks that represent different development card colors.
     */
    public DevCardGrid(List<DevelopmentCard> devCards, int levelsCount, int colorsCount) {
        this.grid = new HashMap<>();
        this.levelsCount = levelsCount;
        this.colorsCount = colorsCount;

        if (!devCards.isEmpty() && colorsCount != 0) {
            for (DevelopmentCard card : devCards) {
                if (!grid.containsKey(card.getColor()))
                    grid.put(card.getColor(),
                            IntStream.range(0, levelsCount + 1)
                                    .mapToObj(i -> i == 0 ? null : new Stack<DevelopmentCard>())
                                    .toList()
                    );
                List<Stack<DevelopmentCard>> column = grid.get(card.getColor());
                Stack<DevelopmentCard> deck = column.get(card.getLevel());
                deck.push(card);
            }
            for (DevCardColor column : grid.keySet())
                for (int cardLevel = 1; cardLevel <= levelsCount; cardLevel++)
                    Collections.shuffle(grid.get(column).get(cardLevel));
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
     * Calculates how many different colors are available for purchase
     *
     * @return number of different colors that are still available for purchase
     */
    public int numOfAvailableColors() {
        return grid.size();
    }

    /**
     * Getter of the deck corresponding the given color and level.
     *
     * @param color the color of choice
     * @param level the level of choice
     * @return the deck corresponding the given color and level
     */
    public Stack<DevelopmentCard> getDeck(DevCardColor color, int level) {
        return grid.get(color).get(level);
    }

    /**
     * Retrieves the top card of each deck (the cards that can be bought during this turn).
     *
     * @return the top card of each deck
     */
    public List<List<DevelopmentCard>> peekDevCards() {
        List<List<DevelopmentCard>> top = new ArrayList<>();
        for (DevCardColor color : grid.keySet()) {
            top.add(grid.get(color)
                    .stream()
                    .filter(Objects::nonNull)
                    .map(s -> s.empty() ? null : s.peek())
                    .toList());
        }
        return List.copyOf(top);
    }

    /**
     * A player buys a card of a given color and level.
     *
     * @param game          the game the player is playing in
     * @param player        the player that wants to buy a card
     * @param color         the color of the card to be bought
     * @param level         the level of the card to be bought
     * @param slotIndex     the position of the dev slot where to put the development card
     * @param resContainers a map of the resource containers where to take the storable resources
     * @throws IllegalCardDepositException      Bought card cannot be placed in the chosen player slot
     * @throws EmptyStackException              No cards available with given color and level
     * @throws IllegalResourceTransferException if the player cannot pay for the card
     */
    public void buyDevCard(Game game, Player player, DevCardColor color, int level, int slotIndex,
                           Map<ResourceContainer, Map<ResourceType, Integer>> resContainers)
            throws IllegalCardDepositException, EmptyStackException, IllegalResourceTransactionReplacementsException, IllegalResourceTransactionContainersException, IllegalResourceTransferException {

        DevelopmentCard card = grid.get(color).get(level).peek();

        player.addToDevSlot(game, slotIndex, card, resContainers);
        grid.get(color).get(level).pop();

        dispatch(new UpdateDevCardGrid(reduce()));
    }

    /**
     * Removes development cards, so that nobody else can purchase them.
     *
     * @param color    the color to be discarded
     * @param quantity the number of cards to be discarded
     */
    public void discardDevCards(DevCardColor color, int quantity) {
        int level = 1;
        while (getDeck(color, level).isEmpty())
            level++;

        while (quantity > 0 && level <= 3) {
            getDeck(color, level).pop();

            if (getDeck(color, level).isEmpty())
                level++;
            quantity--;
        }
        if (quantity > 0)
            grid.remove(color);

        dispatch(new UpdateDevCardGrid(reduce()));
    }

    public ReducedDevCardGrid reduce() {
        Map<String, List<Integer>> topCards = grid.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(e -> e.getKey().getName(), e -> e.getValue().stream().map(s -> {
                    if (s == null)
                        return null;
                    try {
                        return s.peek().getId();
                    } catch (EmptyStackException exception) {
                        return null;
                    }
                }).toList()));
        return new ReducedDevCardGrid(getLevelsCount(), getColorsCount(), topCards);
    }
}
