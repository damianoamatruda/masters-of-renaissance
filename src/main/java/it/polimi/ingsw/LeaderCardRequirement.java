package it.polimi.ingsw;

/**
 * Leader cards can be activated only if the player owns enough of either:
 * <ul>
 * <li>Development cards of a certain color/level
 * <li>Resources of a specified kind
 * </ul>
 * 
 * @see DevelopmentCard
 * @see LeaderCard
 * @see ResourceType
 */
public interface LeaderCardRequirement {
  /**
   * Checks whether the player satisfies the requirements.
   * 
   * @param player  the player to be checked for ownership of the required objects.
   */
  public void checkRequirements(Player player);
}
