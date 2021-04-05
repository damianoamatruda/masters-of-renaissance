package it.polimi.ingsw.devcardcolors;

/**
 * Interface that represents a generic development card color
 */
public abstract class DevCardColor {
    /**
     * @return  a name that will be displayed by the view, based on the concrete color
     */
    abstract String getName();
}
