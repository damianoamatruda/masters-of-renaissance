package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedColor;

import java.util.concurrent.atomic.AtomicReference;

public class DevCardColor extends StringComponent {
    private final String colorName;

    public DevCardColor(String colorName) {
        this.colorName = colorName;
    }

    @Override
    public String getString() {
        AtomicReference<String> str = new AtomicReference<>();

        Cli.getInstance().getViewModel().getDevCardColors().stream()
                .filter(c -> c.getName().equals(colorName))
                .map(ReducedColor::getcolorValue)
                .findAny()
                .ifPresentOrElse(
                        color -> str.set(String.format("\u001B[1m%s%s\u001B[0m", color, colorName)),
                        () -> str.set(colorName));

        return str.get();
    }
}
