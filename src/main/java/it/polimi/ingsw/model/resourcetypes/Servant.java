package it.polimi.ingsw.model.resourcetypes;

/**
 * Resource representing a servant.
 */
public class Servant extends ResourceType {
    /** The name of the resource type. */
    private static final String NAME = "Servant";

    /** The single instance of the class. */
    private static ResourceType instance;

    /**
     * Class constructor.
     */
    private Servant() { }

    @Override
    public boolean isStorable() { return true; }

    /**
     * @return  the single instance of the class
     */
    public static ResourceType getInstance() {
        if (instance == null) instance = new Servant();
        return instance;
    }

    @Override
    public String getName() { return NAME; }

    @Override
    public String toString() { return NAME; }
}
