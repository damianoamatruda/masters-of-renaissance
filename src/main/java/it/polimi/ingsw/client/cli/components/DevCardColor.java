package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedColor;

public class DevCardColor extends StringComponent {
    private final String colorName;

    public DevCardColor(String colorName) {
        this.colorName = colorName;
    }

    @Override
    public String getString(Cli cli) {
        String color = cli.getViewModel().getDevCardColors().stream().filter(c -> c.getName().equals(colorName)).map(ReducedColor::getcolorValue).findAny().orElseThrow();
        return String.format("\u001B[1m%s%s\u001B[0m", color, colorName);
    }
}
