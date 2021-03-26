package it.polimi.ingsw.resourcetypes;

import it.polimi.ingsw.Player;
import it.polimi.ingsw.strongboxes.Strongbox;

public class Shield extends ResourceType {
  /**
   * Single instance of the class
   */
  private static ResourceType resource;

  private Shield() { }

  @Override
  public boolean isBlank() { return false; }

  /**
   * @return  the single instance of this class.
   */
  public static ResourceType getInstance() {
    if (resource == null) resource = new Shield();
    return resource;
  }

  @Override
  public String getName() { return "shield"; }

  @Override
  public void onTaken(Player player, Strongbox strongbox) {
    strongbox.addResource(Shield.getInstance());
  }
}
