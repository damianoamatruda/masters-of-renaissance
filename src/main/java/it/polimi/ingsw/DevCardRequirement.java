package it.polimi.ingsw;

import java.util.Map;

/**
 * Requirement for leader card activation.
 * Specifies what kind of development cards the player must have to be able to activate a leader.
 */
public class DevCardRequirement implements CardRequirement {
    /**
     * The development cards required to activate the leader card.
     */
    private final Map<DevelopmentCard, Integer> devCards;

    /**
     * Class constructor.
     *
     * @param devCards the development cards that form the requirement.
     */
    public DevCardRequirement(Map<DevelopmentCard, Integer> devCards) { this.devCards = devCards; }

    @Override
    public void checkRequirements(Player player) {
        // TODO: Implement
    }

}
