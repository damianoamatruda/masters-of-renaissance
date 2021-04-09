package it.polimi.ingsw.model.cardrequirements;

import it.polimi.ingsw.model.DevelopmentCard;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.leadercards.LeaderCard;
import it.polimi.ingsw.model.resourcetypes.ResourceType;

/**
 * Leader cards can be activated only if the player owns enough of either:
 * <ul>
 *     <li>Development cards of a certain color/level
 *     <li>Resources of a specified kind
 * </ul>
 *
 * @see DevelopmentCard
 * @see LeaderCard
 * @see ResourceType
 */
public interface CardRequirement {
    /**
     * Checks whether the player satisfies the requirements.
     *
     * @param player        the player to be checked for ownership of the required objects.
     * @throws Exception    if the player does not meet the requirements.
     */
    public void checkRequirements(Player player) throws Exception;
}
