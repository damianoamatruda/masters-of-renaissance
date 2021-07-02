package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardColor;

import java.util.concurrent.atomic.AtomicReference;

import static it.polimi.ingsw.client.cli.Cli.boldColor;

/** Cli component that represents card colors as bold and colored strings. */
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
                .map(ReducedDevCardColor::getAnsiColor)
                .findAny()
                .ifPresentOrElse(
                        ansiColor -> str.set(boldColor(colorName, ansiColor)),
                        () -> str.set(colorName));

        return str.get();
    }
}
