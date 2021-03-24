package it.polimi.ingsw;

/**
 * One of the possible colors a development card can have
 */

public class Yellow implements DevCardColor{
    /**
     * @return a name that will be represented as the yellow color by the view
     */
    @Override
    public String getName(){
        return "Yellow";
    }
}
