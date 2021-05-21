package it.polimi.ingsw.client.cli;

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

import it.polimi.ingsw.client.ReducedObjectPrinter;
import it.polimi.ingsw.common.reducedmodel.ReducedGame;

public class Menu implements Renderable {
    private final Map<Character, Entry> entries;

    public Menu(Map<Character, Entry> entries) {
        this.entries = new LinkedHashMap<>(entries);
    }

    @Override
    public void render(Cli cli, PrintStream out, Scanner in, ReducedGame cache, ReducedObjectPrinter printer) {
        // entries.forEach((ch, desc) -> out.printf("[%c] %s%n", ch, desc));
        StringBuilder stringBuilder = new StringBuilder();
        entries.forEach((character, entry) -> stringBuilder.append(String.format("[%c] %s%n", character, entry.description)));
        out.print(Cli.center(stringBuilder.toString()));
        String value;
        do {
            value = cli.prompt(out, in, "");
        } while (value.isBlank() || !entries.containsKey(value.charAt(0)));
        entries.get(value.charAt(0)).action.accept(this);
    }

    public static class Entry {
        private final String description;
        private final Consumer<Menu> action;

        public Entry(String description, Consumer<Menu> action) {
            this.description = description;
            this.action = action;
        }
    }
}
