package it.polimi.ingsw.resourcetypes;

/**
 * Resource representing a shield.
 */
public class Shield extends ResourceType {
    /** The name of the resource type. */
    private static final String NAME = "Shield";

    /** The single instance of the class. */
    private static ResourceType instance;

    /**
     * Class constructor.
     */
    private Shield() { }

    @Override
    public boolean isBlank() { return false; }

    @Override
    public boolean isStorable() { return true; }

    /**
     * @return  the single instance of the class
     */
    public static ResourceType getInstance() {
        if (instance == null) instance = new Shield();
        return instance;
    }

    @Override
    public String getName() { return NAME; }

    @Override
    public String toString() { return NAME; }
}
