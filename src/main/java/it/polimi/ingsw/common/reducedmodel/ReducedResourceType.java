package it.polimi.ingsw.common.reducedmodel;

public class ReducedResourceType {
    private final String name;
    private final String ansiColor;
    private final boolean isStorable;
    private final boolean isGiveableToPlayer;
    private final boolean isTakeableFromPlayer;

    public ReducedResourceType(String name, String ansiColor, boolean isStorable, boolean isGiveableToPlayer, boolean isTakeableFromPlayer) {
        if (ansiColor == null)
            throw new IllegalArgumentException("Null color constructing reduced resource type.");

        this.name = name;
        this.ansiColor = ansiColor;
        this.isStorable = isStorable;
        this.isGiveableToPlayer = isGiveableToPlayer;
        this.isTakeableFromPlayer = isTakeableFromPlayer;
    }

    public String getName() {
        return name;
    }

    public String getAnsiColor() {
        return ansiColor;
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
