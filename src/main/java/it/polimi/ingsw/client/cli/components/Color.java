package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedColor;

public class Color extends StringComponent {
    private final String colorName;

    public Color(String colorName) {
        this.colorName = colorName;
    }

    @Override
    public String getString(Cli cli) {
        String color = cli.getViewModel().getDevCardColors().stream().filter(c -> c.getName().equals(colorName)).map(ReducedColor::getcolorValue).findAny().orElseThrow();
        return "\u001B[1m" + color + colorName + "\u001B[0m"; // "⚫"
    }
}
