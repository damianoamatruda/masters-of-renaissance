package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.reducedmodel.ReducedColor;

/**
 * This class represents a development card color.
 */
public class DevCardColor {
    /** The name of the development card color. */
    private final String name;

    private final String colorValue;

    /**
     * Constructor of the development card color.
     *
     * @param name the name of the development card color
     */
    public DevCardColor(String name) {
        this.name = name;
        this.colorValue = "#ffffff";
    }

    /**
     * Returns the name of the development card color.
     *
     * @return the name of the development card color
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public ReducedColor reduce() {
        return new ReducedColor(name, colorValue);
    }
}
