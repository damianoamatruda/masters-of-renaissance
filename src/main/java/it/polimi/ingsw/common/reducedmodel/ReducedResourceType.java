package it.polimi.ingsw.common.reducedmodel;

public class ReducedResourceType {
    private String name, hex;

    public ReducedResourceType(String name, String hex) {
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
