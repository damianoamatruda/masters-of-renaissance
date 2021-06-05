package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.client.cli.Renderable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Menu implements Renderable {
    private final Map<Character, Entry> entries;

    public Menu(Map<Character, Entry> entries) {
        this.entries = new LinkedHashMap<>(entries);
    }

    @Override
    public void render(Cli cli) {
        StringBuilder stringBuilder = new StringBuilder();
        entries.forEach((character, entry) -> stringBuilder.append(String.format("[%c] %s%n", character, entry.description)));
        cli.getOut().print(Cli.center(stringBuilder.toString()));
        String value;
        do {
            value = cli.prompt("");
        } while (value.isBlank() || !entries.containsKey(value.charAt(0)));
        entries.get(value.charAt(0)).action.accept(cli);
    }

    public static class Entry {
        private final String description;
        private final Consumer<Cli> action;

        public Entry(String description, Consumer<Cli> action) {
            this.description = description;
            this.action = action;
        }
    }
}
