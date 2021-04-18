package it.polimi.ingsw.server.model;

/**
 * This class represents a generic development card color.
 */
public class DevCardColor {
    /** The name of the color. */
    private final String name;

    /**
     * Constructor of the development card color.
     *
     * @param name the name of the development card color
     */
    public DevCardColor(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the development card color.
     *
     * @return the name of the resource associated with the class, for UI purposes only
     */
    public String getName() {
        return name;
    }
}
