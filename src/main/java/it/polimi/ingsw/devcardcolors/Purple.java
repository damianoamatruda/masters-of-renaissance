package it.polimi.ingsw.devcardcolors;

/**
 * One of the possible colors a development card can have
 */
public class Purple extends DevCardColor {

    /**
     * @return a name that will be represented as the purple color by the view
     */
    @Override
    public String getName(){
        return "Purple";
    }

    /**
     * @return  the single instance of this class.
     */
    public static DevCardColor getInstance() {
        if (color == null) color = new Purple();
        return color;
    }
}
