package it.polimi.ingsw.client.cli.components;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.client.cli.Cli.*;

/** Cli component that boxes other components, by adding borders to their string representation. */
public class Box extends StringComponent {
    private static final int defaultPadding = 1;
    private final StringComponent component;
    private final int padding;
    private final int width;
    private final int height;

    public Box(StringComponent component, int width, int height, int padding) {
        this.component = component;
        this.padding = padding >= 0 ? padding : defaultPadding;
        this.width = width;
        this.height = height;
    }

    public Box(StringComponent component, int width, int height) {
        this(component, width, height, -1);
    }

    public Box(StringComponent component, int width) {
        this(component, width, -1);
    }

    public Box(StringComponent component) {
        this(component, -1);
    }

    @Override
    public String getString() {
        StringBuilder stringBuilder = new StringBuilder();

        String componentString = component != null ? component.getString() : "";

        int width = (this.width < 0 ? maxLineWidth(componentString) : this.width) + padding * 2;
        int height = this.height < 0 ? Math.toIntExact(componentString.lines().count()) : this.height;

        List<String> lines = new ArrayList<>(componentString.lines().toList());
        for (int i = lines.size(); i < height; i++)
            lines.add("");

        stringBuilder.append("┌").append(slimLineNoNewLine(width)).append("┐").append("\n");

        lines.forEach(line ->
                stringBuilder.append("│").append(center(left(line, maxLineWidth(componentString)), width)).append("│").append("\n"));

        stringBuilder.append("└").append(slimLineNoNewLine(width)).append("┘");

        return stringBuilder.toString();
    }
}
