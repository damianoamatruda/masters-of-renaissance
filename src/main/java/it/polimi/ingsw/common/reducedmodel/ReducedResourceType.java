package it.polimi.ingsw.common.reducedmodel;

public class ReducedResourceType {
    private String name, colorValue;
    private boolean isStorable;

    public ReducedResourceType(String name, String colorValue, boolean isStorable) {
        this.name = name;
        this.colorValue = colorValue;
        this.isStorable = isStorable;
    }

    public String getName() {
        return name;
    }

    public String getColorValue() {
        return colorValue;
    }

    public boolean isStorable() {
        return isStorable;
    }
}
