package it.polimi.ingsw.model.resourcetypes;

/**
 * Resource that can be substituted with another resource.
 */
public class Zero extends ResourceType {
    /** The name of the resource type. */
    private static final String NAME = "Zero";

    /** The single instance of the class. */
    private static ResourceType instance;

    /**
     * Class constructor.
     */
    private Zero() { }

    @Override
    public boolean isBlank() { return true; }

    @Override
    public boolean isStorable() { return false; }

    /**
     * @return  the single instance of the class
     */
    public static ResourceType getInstance() {
        if (instance == null) instance = new Zero();
        return instance;
    }

    @Override
    public String getName() { return NAME; }

    @Override
    public String toString() { return NAME; }
}
