package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.client.cli.Renderable;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Menu implements Renderable {
    private final Map<Character, Entry> entries;

    public Menu(Map<Character, Entry> entries) {
        if (!entries.keySet().stream().map(Character::toLowerCase).allMatch(new HashSet<>()::add))
            throw new IllegalArgumentException("Duplicate entries.");

        this.entries = new LinkedHashMap<>();
        for (Character c : entries.keySet())
            this.entries.put(Character.toUpperCase(c), entries.get(c));
    }

    @Override
    public void render(Cli cli) {
        StringBuilder stringBuilder = new StringBuilder();
        entries.forEach((c, entry) -> stringBuilder.append(String.format("[%c] %s%n", c, entry.description)));
        cli.getOut().print(Cli.center(stringBuilder.toString()));
        Character c;
        do {
            String value = cli.prompt("");
            c = !value.isBlank() ? Character.toUpperCase(value.charAt(0)) : null;
        } while (c == null || !entries.containsKey(c));
        entries.get(c).action.accept(cli);
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
