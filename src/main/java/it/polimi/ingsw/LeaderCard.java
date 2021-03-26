package it.polimi.ingsw;

import java.util.Map;

public abstract class LeaderCard extends Card {
  private final ResourceType resource;
  private final LeaderCardRequirement requirement;
  /**
   * The card's status. If active, the ability can be triggered.
   */
  private boolean isActive = false;

  /**
   * Class constructor.
   * 
   * @param resource      the resource bound to the card.
   *                      The card's ability is restricted to acting on this resource type only.
   * @param requirement   the requirement to be satisfied for card activation.
   * @param victoryPoints the victory points associated with the card.
   */
  public LeaderCard(ResourceType resource, LeaderCardRequirement requirement, int victoryPoints) {
    super(victoryPoints);
    this.requirement = requirement;
    this.resource = resource;
  }

  /** 
   * Returns the activation status of the card.
   * It doesn't have to be checked prior to the card's use, as it is used internally
   * to check whether the card's ability can be used
   * 
   * @return the card's activation status.
   */
  public boolean isActive() { return isActive; }

  /**
   * Activates the card, enabling its effects.
   */
  public void activate() { isActive = true; }

  /**
   * @return  the resource tied to the card. It binds the card's ability to a specific resource type.
   */
  public ResourceType getResource() { return resource; }
  /**
   * Executes the discarding routine for leader cards.
   * 
   * @param player  the player that discards the card.
   *                All routine effects are applied on this player.
   */
  public void onDiscarded(Player player) {
    //TODO implement
  }

  /**
   * @return  the shelf pertaining to the leader.
   */
  public ResourceShelf getDepot() { return null; }

  /**
   * Applies the leader card's discount.
   * 
   * @param devCardCost the cost to apply the discount on.
   * @return  the cost discounted by the card's ability's amount.
   */
  public Map<ResourceType, Integer> getDevCardCost(Map<ResourceType, Integer> devCardCost) { return devCardCost; }

  /**
   * Returns the <code>Production</code> associated with the leader card.
   * 
   * @return the Production object of the leader card.
   */
  public Production getProduction() { return null; }

  /**
   * Processes <code>Zero</code> resources. If the leader is a ZeroLeader, 
   * they are replaced by the <code>ResourceType</code> of the leader card.
   * 
   * @param resources the resources to be processed.
   * @param zeros     the resources to substitute to the zeros.
   *                  If there's resources of different type from the leader's, they will be ignored.
   * @return          the resources transformed as per the leader's ability.
   */
  public Map<ResourceType, Integer> processZeros(Map<ResourceType, Integer> resources, Map<ResourceType, Integer> zeros) { return resources; }
}
