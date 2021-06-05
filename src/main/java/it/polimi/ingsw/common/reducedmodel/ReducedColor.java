package it.polimi.ingsw.common.reducedmodel;

public class ReducedColor {
    private final String name, colorValue;

    public ReducedColor(String name, String colorValue) {
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
