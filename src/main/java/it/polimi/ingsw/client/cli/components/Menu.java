package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.client.cli.Renderable;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static it.polimi.ingsw.client.cli.Cli.center;

/**
 * CLI component that renders a menu.
 */
public class Menu implements Renderable {
    private final Map<Character, Entry> entries;
    private final Runnable onBack;

    public Menu(Map<Character, Entry> entries, Runnable onBack) {
        if (!entries.keySet().stream().map(Character::toLowerCase).allMatch(new HashSet<>()::add))
            throw new IllegalArgumentException("Duplicate entries.");

        this.entries = new LinkedHashMap<>();
        for (Character c : entries.keySet())
            this.entries.put(Character.toUpperCase(c), entries.get(c));

        this.onBack = onBack;
    }

    @Override
    public void render() {
        Cli cli = Cli.getInstance();

        StringBuilder stringBuilder = new StringBuilder();

        entries.forEach((c, entry) -> stringBuilder.append(String.format("[%c] %s%n", c, entry.description)));
        cli.getOut().println(center(stringBuilder.toString()));
        cli.getOut().println();
        AtomicBoolean valid = new AtomicBoolean(false);
        while (!valid.get()) {
            cli.prompt("").ifPresentOrElse(input -> {
                Character c = !input.isBlank() ? Character.toUpperCase(input.charAt(0)) : null;
                if (c != null && entries.containsKey(c)) {
                    entries.get(c).onAction.run();
                    valid.set(true);
                }
            }, () -> {
                onBack.run();
                valid.set(true);
            });
        }
    }

    public static class Entry {
        private final String description;
        private final Runnable onAction;

        public Entry(String description, Runnable onAction) {
            this.description = description;
            this.onAction = onAction;
        }
    }
}
