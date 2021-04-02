package it.polimi.ingsw.devcardcolors;

/**
 * One of the possible colors a development card can have
 */
public class Blue extends DevCardColor {
    /**
     * Single instance of the class
     */
    private static DevCardColor color;

    /**
     * @return a name that will be represented as the blue color by the view
     */
    @Override
    public String getName(){
        return "Blue";
    }

    /**
     * @return  the single instance of this class.
     */
    public static DevCardColor getInstance() {
        if (color == null) color = new Blue();
        return color;
    }
}
