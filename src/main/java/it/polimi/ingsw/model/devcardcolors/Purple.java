package it.polimi.ingsw.model.devcardcolors;

/**
 * One of the possible colors a development card can have.
 */
public class Purple extends DevCardColor {
    /** The name of the color. */
    private static final String NAME = "Purple";

    /** The single instance of the class. */
    private static DevCardColor instance;

    /**
     * @return  the single instance of the class
     */
    public static DevCardColor getInstance() {
        if (instance == null) instance = new Purple();
        return instance;
    }

    @Override
    public String getName(){
        return NAME;
    }

    @Override
    public String toString(){
        return NAME;
    }
}