package it.polimi.ingsw.devcardcolors;

/**
 * One of the possible colors a development card can have
 */
public class Green implements DevCardColor{
    /**
     * @return a name that will be represented as the green color by the view
     */
    @Override
    public String getName(){
        return "Green";
    }
}
