package it.polimi.ingsw;

public class Servant extends ResourceType {
  /**
   * Single instance of the class
   */
  private static ResourceType resource;

  private Servant() { }

  @Override
  public boolean isBlank() { return false; }

  /**
   * @return  the single instance of this class.
   */
  public static ResourceType getInstance() {
    if (resource == null) resource = new Servant();
    return resource;
  }

  @Override
  public String getName() { return "servant"; }

  @Override
  public void onTaken(Player player, Strongbox strongbox) {
    strongbox.addResource(Servant.getInstance());
  }
}
