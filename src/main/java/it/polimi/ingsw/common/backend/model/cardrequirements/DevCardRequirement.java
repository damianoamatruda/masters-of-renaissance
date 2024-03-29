package it.polimi.ingsw.common.backend.model.cardrequirements;

import it.polimi.ingsw.common.backend.model.DevCardColor;
import it.polimi.ingsw.common.backend.model.DevelopmentCard;
import it.polimi.ingsw.common.backend.model.Player;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirement;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirementEntry;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceRequirement;

import java.util.*;
import java.util.stream.Collectors;

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
        entryList = new HashSet<>(requirements);
    }

    @Override
    public void checkRequirements(Player player) throws CardRequirementsNotMetException {
        /* gather all the player's dev cards
         * from them another set of entries will be extracted
         * then the two sets are compared to see if all required entries
         * are present in the ones extracted from the player */

        List<DevelopmentCard> playerCards = new ArrayList<>();

        playerCards.addAll(player.getDevSlots().stream().flatMap(Stack::stream).toList());

        Set<Entry> playerState = new HashSet<>(),
                reqCopy = new HashSet<>(entryList.stream()
                        .filter(e -> e.quantity > 0)
                        .collect(Collectors.toUnmodifiableSet())),
                missing = new HashSet<>();

        playerCards.forEach(card -> {
            Entry currCard = new Entry(card.getColor(), card.getLevel(), 1);

            playerState.stream().filter(e -> e.equals(currCard))
                    .findAny()
                    .ifPresentOrElse(
                            e -> e.quantity++,
                            () -> playerState.add(currCard));
        });

        for (Entry entry : reqCopy) {
            /* Entry not found in player's cards -> requirements not satisfied */
            if (playerState.stream().noneMatch(e -> e.equals(entry)))
                missing.add(entry);
                /* If the entry is found, the quantity of cards the player owns in that entry is subtracted from the requirements. */
            else
                playerState.stream().filter(e -> e.equals(entry)).findAny().ifPresent(e -> {
                    if (entry.quantity - e.quantity > 0)
                        missing.add(new Entry(entry.color, entry.level, entry.quantity - e.quantity));
                });
        }

        /* If the missing set is not empty, the requirements have not been met. */
        if (!missing.isEmpty())
            throw new CardRequirementsNotMetException(missing);
    }

    @Override
    public ReducedResourceRequirement reduceRR() {
        return null;
    }

    @Override
    public ReducedDevCardRequirement reduceDR() {
        return new ReducedDevCardRequirement(entryList.stream().map(Entry::reduce).toList());
    }

    /**
     * Models a requirement entry. It mimics a double-keyed map, with color and level as keys and quantity as value.
     */
    public static class Entry {
        private final DevCardColor color;
        private final int level;
        private int quantity = 0;

        /**
         * Class constructor. Creates an Entry with set color and level, and zero as its quantity.
         *
         * @param color the card color to be matched.
         * @param level the card level to be matched.
         */
        public Entry(DevCardColor color, int level) {
            this.color = color;
            this.level = level;
        }

        /**
         * Class constructor. Creates an Entry with set color, level and quantity.
         *
         * @param color    the card color to be matched.
         * @param level    the card level to be matched.
         * @param quantity the quantity of cards to be matched.
         */
        public Entry(DevCardColor color, int level, int quantity) {
            this.color = color;
            this.level = level;
            this.quantity = quantity;
        }

        public ReducedDevCardRequirementEntry reduce() {
            return new ReducedDevCardRequirementEntry(color.getName(), level, quantity);
        }

        /**
         * Sets the new quantity of cards in the entry.
         *
         * @param newQuantity the new quantity of cards the entry holds.
         * @return the updated entry.
         */
        public Entry setQuantity(int newQuantity) {
            quantity = newQuantity;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Entry))
                return false;

            /* two entries are defined as equal if
             * if the color is the same and
             * either the levels are the same, or
             *      either the requirement's or the card's level are 0 (which means "any") */
            return ((Entry) o).color == this.color &&
                    (((Entry) o).level == 0 || this.level == 0 || ((Entry) o).level == this.level);
        }
    }
}