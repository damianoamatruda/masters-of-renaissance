package it.polimi.ingsw.common.reducedmodel;

public class ReducedResourceType {
    private String name, colorValue;

    public ReducedResourceType(String name, String colorValue) {
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
