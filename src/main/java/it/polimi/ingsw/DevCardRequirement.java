package it.polimi.ingsw;

import java.util.Map;

public class DevCardRequirement implements LeaderCardRequirement {
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

    /**
     * Checks whether the player owns the development cards necessary to activate the leader card.
     *
     * @param player  the player to be checked for development card ownership.
     */
    @Override
    public void checkRequirements(Player player) {
        // TODO Auto-generated method stub

    }

}
