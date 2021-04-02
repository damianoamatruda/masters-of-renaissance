package it.polimi.ingsw.devcardcolors;

/**
 * One of the possible colors a development card can have
 */
public class Green extends DevCardColor {

    /**
     * @return a name that will be represented as the green color by the view
     */
    @Override
    public String getName(){
        return "Green";
    }

    /**
     * @return  the single instance of this class.
     */
    public static DevCardColor getInstance() {
        if (color == null) color = new Green();
        return color;
    }
}
