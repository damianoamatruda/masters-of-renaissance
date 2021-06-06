package it.polimi.ingsw.common.reducedmodel;

public class ReducedResourceType {
    private final String name;
    private final String colorValue;
    private final boolean isStorable;
    private final boolean isGiveableToPlayer;
    private final boolean isTakeableFromPlayer;

    public ReducedResourceType(String name, String colorValue, boolean isStorable, boolean isGiveableToPlayer, boolean isTakeableFromPlayer) {
        this.name = name;
        this.colorValue = colorValue;
        this.isStorable = isStorable;
        this.isGiveableToPlayer = isGiveableToPlayer;
        this.isTakeableFromPlayer = isTakeableFromPlayer;
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

    public boolean isGiveableToPlayer() {
        return isGiveableToPlayer;
    }

    public boolean isTakeableFromPlayer() {
        return isTakeableFromPlayer;
    }
}
