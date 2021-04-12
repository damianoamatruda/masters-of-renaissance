package it.polimi.ingsw.model.cardrequirements;

import java.util.*;

import it.polimi.ingsw.model.DevelopmentCard;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.devcardcolors.DevCardColor;

/**
 * Requirement for leader card activation. Specifies what kind of development cards the player must have to be able to
 * activate a leader.
 */
public class DevCardRequirement implements CardRequirement {
    /** The development cards required to activate the leader card. */
    private final Set<Entry> entryList;

    /**
     * Class constructor.
     *
     * @param requirements the development cards that form the requirement.
     */
    public DevCardRequirement(Set<Entry> requirements) {
        entryList = requirements;
    }

    @Override
    public void checkRequirements(Player player) throws Exception {
        List<DevelopmentCard> playerCards = new ArrayList<>();

        for (int i = 0; i < player.getDevSlots().size(); i++)
            playerCards.addAll(player.getDevSlots().get(i));
        
        Set<Entry> playerState = new HashSet<>(),
                   reqCopy = new HashSet<>(entryList);

        playerCards.forEach(card -> {
            Entry currCard = new Entry(card.getColor(), card.getLevel());
            // if not present add a new entry
            if (!playerState.contains(currCard))
                playerState.add(currCard.setAmount(1));
            // else increment the amount available
            else playerState.stream().filter(e -> e.equals(currCard)).findFirst().get().amount++;
        });

        for (Entry entry : reqCopy) {
            if (!playerState.stream().anyMatch(e -> e.equals(entry))) throw new Exception(); // entry not found in player's cards -> requirements not satisfied

            entry.setAmount(entry.amount - playerState.stream().filter(e -> e.equals(entry)).findFirst().get().amount);
        }

        // if the req hold positive amounts it means they haven't been satisfied
        if (reqCopy.stream().filter(entry -> entry.amount > 0).count() > 0) throw new Exception();
    }

    /**
     * Models a requirement entry.
     * It mimicks a double-keyed map, with color and level as keys and amount as value.
     */
    public static class Entry {
        private final DevCardColor color;
        private final int level;
        private int amount = 0;

        /**
         * Class constructor. Creates an Entry with set color and level, and zero as its amount.
         * 
         * @param color the card color to be matched.
         * @param level the card level to be matched.
         */
        public Entry(DevCardColor color, int level) {
            this.color = color; this.level = level;
        }

        /**
         * Class constructor. Creates an Entry with set color, level and amount.
         * 
         * @param color     the card color to be matched.
         * @param level     the card level to be matched.
         * @param amount    the amount of cards to be matched.
         */
        public Entry(DevCardColor color, int level, int amount) {
            this.color = color; this.level = level; this.amount = amount;
        }

        /**
         * Sets the new amount of cards in the entry.
         * 
         * @param newAmount the new amount of cards the entry holds.
         * @return          the updated entry.
         */
        public Entry setAmount(int newAmount) { amount = newAmount; return this; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Entry)) return false;

            return  ((Entry)o).color == this.color &&
                    (((Entry)o).level == 0 || this.level == 0 || ((Entry)o).level == this.level);
        }
    }
}