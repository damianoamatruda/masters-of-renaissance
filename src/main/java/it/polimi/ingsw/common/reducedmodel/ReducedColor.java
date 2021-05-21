package it.polimi.ingsw.common.reducedmodel;

public class ReducedColor {
    private String name, hex;

    public ReducedColor(String name, String hex) {
        this.name = name;
        this.hex = hex;
    }

    public String getName() {
        return name;
    }

    public String getHex() {
        return hex;
    }
}
