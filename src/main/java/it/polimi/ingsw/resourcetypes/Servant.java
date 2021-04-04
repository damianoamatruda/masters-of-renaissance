package it.polimi.ingsw.resourcetypes;

/**
 * Resource representing a servant.
 */
public class Servant extends ResourceType {
    /**
     * Single instance of the class
     */
    private static ResourceType resource;

    private Servant() { }

    @Override
    public boolean isBlank() { return false; }

    @Override
    public boolean isStorable() { return true; }

    /**
     * @return  the single instance of this class
     */
    public static ResourceType getInstance() {
        if (resource == null) resource = new Servant();
        return resource;
    }

    @Override
    public String getName() { return "servant"; }

}
