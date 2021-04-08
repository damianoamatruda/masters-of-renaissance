package it.polimi.ingsw.model.resourcetypes;

/**
 * Resource representing a coin.
 */
public class Coin extends ResourceType {
    /** The name of the resource type. */
    private static final String NAME = "Coin";

    /** The single instance of the class. */
    private static ResourceType instance;

    /**
     * Class constructor.
     */
    private Coin() { }

    @Override
    public boolean isStorable() { return true; }

    /**
     * @return  the single instance of the class
     */
    public static ResourceType getInstance() {
        if (instance == null) instance = new Coin();
        return instance;
    }

    @Override
    public String getName() { return NAME; }

    @Override
    public String toString() { return NAME; }
}
