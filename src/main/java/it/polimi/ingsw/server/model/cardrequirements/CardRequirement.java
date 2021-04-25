package it.polimi.ingsw.server.model.cardrequirements;

import it.polimi.ingsw.server.model.DevelopmentCard;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.leadercards.LeaderCard;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;

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
     * @param player the player to be checked for ownership of the required objects.
     * @throws RequirementsNotMetException if the player does not meet the requirements.
     */
    void checkRequirements(Player player) throws RequirementsNotMetException;
}
