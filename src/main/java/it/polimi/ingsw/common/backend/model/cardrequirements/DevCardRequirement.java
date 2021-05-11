package it.polimi.ingsw.common.backend.model.cardrequirements;

import it.polimi.ingsw.common.backend.model.DevCardColor;
import it.polimi.ingsw.common.backend.model.DevelopmentCard;
import it.polimi.ingsw.common.backend.model.Player;
import it.polimi.ingsw.common.reducedmodel.ReducedCardRequirement;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirement;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirementEntry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Requirement for leader card activation. Specifies what kind of development cards the player must have to be able to
 * activate a leader.
 *
 * @see it.polimi.ingsw.common.backend.model.leadercards.LeaderCard
 * @see CardRequirement
 * @see Player
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
    public void checkRequirements(Player player) throws CardRequirementsNotMetException {
        /* gather all the player's dev cards
         * from them another set of entries will be extracted
         * then the two sets are compared to see if all required entries
         * are present in the ones extracted from the player */

        List<DevelopmentCard> playerCards = new ArrayList<>();
        for (int i = 0; i < player.getDevSlots().size(); i++)
            playerCards.addAll(player.getDevSlots().get(i));

        Set<Entry> playerState = new HashSet<>(),
                reqCopy = new HashSet<>(entryList), // never touch the original set
                missing = new HashSet<>();

        playerCards.forEach(card -> {
            Entry currCard = new Entry(card.getColor(), card.getLevel());
            // if not present add a new entry
            if (!playerState.contains(currCard))
                playerState.add(currCard.setAmount(1));
                // else increment the amount available
            else playerState.stream().filter(e -> e.equals(currCard)).findFirst().ifPresent(e -> e.amount++);
        });

        for (Entry entry : reqCopy) {
            // entry not found in player's cards -> requirements not satisfied
            if (playerState.stream().noneMatch(e -> e.equals(entry)))
                missing.add(entry);
                // if the entry is found the amount of cards the player owns in that entry is subtracted from the requirements
            else {
                playerState.stream().filter(e -> e.equals(entry)).findFirst().ifPresent(e -> {
                    if (entry.amount - e.amount > 0)
                        missing.add(new Entry(entry.color, entry.level, entry.amount - e.amount));
                });
            }
        }

        // if the missing set isn't empty the requirements haven't been met
        if (missing.size() != 0) {
            String msg = String.format("\nPlayer %s does not satisfy the following entries:", player.getNickname());

            for (Entry e : new ArrayList<>(missing)) {
                msg = msg.concat(String.format("\nColor %s, level %d, missing %s", e.color.getName(), e.level, e.amount));
            }

            throw new CardRequirementsNotMetException("development card", msg);
        }
    }

    @Override
    public ReducedCardRequirement reduce() {
        return new ReducedDevCardRequirement(entryList.stream().map(e -> e.reduce()).toList());
    }

    /**
     * Models a requirement entry. It mimics a double-keyed map, with color and level as keys and amount as value.
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
            this.color = color;
            this.level = level;
        }

        /**
         * Class constructor. Creates an Entry with set color, level and amount.
         *
         * @param color  the card color to be matched.
         * @param level  the card level to be matched.
         * @param amount the amount of cards to be matched.
         */
        public Entry(DevCardColor color, int level, int amount) {
            this.color = color;
            this.level = level;
            this.amount = amount;
        }

        public ReducedDevCardRequirementEntry reduce() {
            return new ReducedDevCardRequirementEntry(color.getName(), level, amount);
        }

        /**
         * Sets the new amount of cards in the entry.
         *
         * @param newAmount the new amount of cards the entry holds.
         * @return the updated entry.
         */
        public Entry setAmount(int newAmount) {
            amount = newAmount;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Entry)) return false;

            /* two entries are defined as equal if
             * if the color is the same and
             * either the levels are the same, or
             *      either the requirement's or the card's level are 0 (which means "any") */
            return ((Entry) o).color == this.color &&
                    (((Entry) o).level == 0 || this.level == 0 || ((Entry) o).level == this.level);
        }
    }
}