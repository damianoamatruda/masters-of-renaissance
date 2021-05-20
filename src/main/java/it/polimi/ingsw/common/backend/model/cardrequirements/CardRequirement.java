package it.polimi.ingsw.common.backend.model.cardrequirements;

import it.polimi.ingsw.common.backend.model.DevelopmentCard;
import it.polimi.ingsw.common.backend.model.Player;
import it.polimi.ingsw.common.backend.model.leadercards.LeaderCard;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirement;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceRequirement;

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
     * @throws CardRequirementsNotMetException if the player does not meet the requirements.
     */
    void checkRequirements(Player player) throws CardRequirementsNotMetException;

    ReducedResourceRequirement reduceRR();
    ReducedDevCardRequirement reduceDR();
}
