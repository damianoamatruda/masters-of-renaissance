package it.polimi.ingsw;

public abstract class ResourceType {
  /**
   * Class constructor.
   */
  private ResourceType() { }

  /**
   * @return the single instance of this class.
   */
  public static ResourceType getInstance() { return null; }

  /**
   * @return  the name of the resource associated with the class.
   *          For UI purposes only.
   */
  public abstract String getName();

  /**
   * @return  whether the resource can be replaced with another resource.
   */
  public boolean isBlank() { return false; }

  /**
   * Routine for consuming the resource. Its effect is adding the resource to the player.
   * 
   * @param player    the player the resource goes to.
   * @param strongbox the storage in which the reource is deposited, if applicable.
   */
  public abstract void onTaken(Player player, Strongbox strongbox);
}
