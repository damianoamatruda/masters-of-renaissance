package it.polimi.ingsw.common.reducedmodel;

public class ReducedDevCardColor {
    private final String name, colorValue;

    public ReducedDevCardColor(String name, String colorValue) {
        if (name == null)
            throw new IllegalArgumentException("Null name constructing reduced color.");
        if (colorValue == null)
            throw new IllegalArgumentException("Null color value constructing reduced color.");

        this.name = name;
        this.colorValue = colorValue;
    }

    public String getName() {
        return name;
    }

    public String getcolorValue() {
        return colorValue;
    }
}
