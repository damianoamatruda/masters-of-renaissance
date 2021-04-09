package it.polimi.ingsw.model;

import java.util.*;

import it.polimi.ingsw.model.devcardcolors.DevCardColor;

/**
 * Requirement for leader card activation. Specifies what kind of development cards the player must have to be able to
 * activate a leader.
 */
public class DevCardRequirement implements CardRequirement {
    /** The development cards required to activate the leader card. */
    private final List<DevCardRequirementEntry> entryList;

    /**
     * Class constructor.
     *
     * @param requirements the development cards that form the requirement.
     */
    public DevCardRequirement(Map<Map<DevCardColor, Integer>, Integer> requirements) {
        entryList = buildRequirements(requirements);
    }

    // TODO: Add Javadoc
    private List<DevCardRequirementEntry> buildRequirements(Map<Map<DevCardColor, Integer>, Integer> reqs) {
        List<DevCardRequirementEntry> entries = new ArrayList<>();

        reqs.forEach((map, amount) -> map.forEach((color, level) -> entries.add(new DevCardRequirementEntry(color, level, amount))));
        
        return entries;
    }

    @Override
    public void checkRequirements(Player player) throws Exception {
        List<DevelopmentCard> playerCards = new ArrayList<>();

        for (int i = 0; i < player.getDevSlots().size(); i++)
            playerCards.addAll(player.getDevSlots().get(i));
        
        List<DevCardRequirementEntry>   playerState = new ArrayList<>(),
                                        reqCopy = new ArrayList<>(entryList);

        playerCards.forEach(card -> {
            int index = playerState.indexOf(new DevCardRequirementEntry(card.getColor(), card.getLevel()));
            // if not present add a new entry
            if (index < 0) playerState.add(new DevCardRequirementEntry(card.getColor(), card.getLevel(), 1));
            // else increment the amount available
            else  playerState.add(index, playerState.get(index).setAmount(playerState.get(index).amount + 1));
        });

        for (DevCardRequirementEntry entry : reqCopy) {
            int index = playerState.indexOf(entry);
            if (index < 0) throw new Exception(); // entry not found in player's cards -> requirements not satisfied

            entry.setAmount(entry.amount - playerState.get(index).amount);
        }

        // if the req hold positive amounts it means they haven't been satisfied
        if (reqCopy.stream().filter(entry -> entry.amount > 0).count() > 0) throw new Exception();
    }
}