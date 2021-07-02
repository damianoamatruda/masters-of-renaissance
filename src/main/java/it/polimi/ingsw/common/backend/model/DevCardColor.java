package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.reducedmodel.ReducedDevCardColor;

/**
 * This class represents a development card color.
 */
public class DevCardColor {
    /** The name of the development card color. */
    private final String name;

    // TODO: Javadoc
    private final String ansiColor;

    /**
     * Constructor of the development card color.
     *
     * @param name      the name of the development card color
     * @param ansiColor TODO
     */
    public DevCardColor(String name, String ansiColor) {
        this.name = name;
        this.ansiColor = ansiColor;
    }

    /**
     * Returns the name of the development card color.
     *
     * @return the name of the development card color
     */
    public String getName() {
        return name;
    }

    public ReducedDevCardColor reduce() {
        return new ReducedDevCardColor(name, ansiColor);
    }
}
