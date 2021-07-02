package it.polimi.ingsw.common.reducedmodel;

public class ReducedDevCardColor {
    private final String name;
    private final String ansiColor;

    public ReducedDevCardColor(String name, String ansiColor) {
        if (name == null)
            throw new IllegalArgumentException("Null name constructing reduced color.");
        if (ansiColor == null)
            throw new IllegalArgumentException("Null color value constructing reduced color.");

        this.name = name;
        this.ansiColor = ansiColor;
    }

    public String getName() {
        return name;
    }

    public String getAnsiColor() {
        return ansiColor;
    }
}
