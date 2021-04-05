package it.polimi.ingsw.resourcetypes;

/**
 * Resource representing a stone.
 */
public class Stone extends ResourceType {
    /** The name of the resource type. */
    private static final String NAME = "Stone";

    /** The single instance of the class. */
    private static ResourceType instance;

    /**
     * Class constructor.
     */
    private Stone() { }

    @Override
    public boolean isBlank() { return false; }

    @Override
    public boolean isStorable() { return true; }

    /**
     * @return  the single instance of the class
     */
    public static ResourceType getInstance() {
        if (instance == null) instance = new Stone();
        return instance;
    }

    @Override
    public String getName() { return NAME; }

    @Override
    public String toString() { return NAME; }
}
