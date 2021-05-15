package it.polimi.ingsw.client.cli;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.client.ClientServerHandler;
import it.polimi.ingsw.client.Ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class Cli implements Ui {
    private final static String jsonConfigPath = "/config/server.json";
    static final int width = 80;

    /** The current state of the interface. */
    private CliState state;

    public Cli() {
        this.state = new SplashState();
    }

    static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    static String center(String s) {
        if (s.lines().count() == 0)
            return "";

        int maxLineWidth = s.lines().mapToInt(String::length).max().orElseThrow();

        if (maxLineWidth > width)
            throw new IllegalArgumentException();

        int marginLeft = (width - maxLineWidth) / 2;

        StringBuilder stringBuilder = new StringBuilder();
        s.lines().forEachOrdered(line -> stringBuilder.append(" ".repeat(marginLeft)).append(line).append("\n"));
        return stringBuilder.toString();
    }

    static void clear(PrintStream out) {
        out.print("\033[H\033[2J");
        out.flush();
    }

    static void pause(Scanner in) {
        in.nextLine();
    }

    static String prompt(PrintStream out, Scanner in, String prompt, String defaultValue) {
        out.printf("%s (default: %s): ", prompt, defaultValue);
        String value = in.nextLine();
        return !value.isBlank() ? value : defaultValue;
    }

    static Character menu(PrintStream out, Scanner in, Map<Character, String> entries) {
        entries.forEach((ch, desc) -> out.printf("[%c] %s%n", ch, desc));
        String value;
        do {
            value = in.nextLine();
        } while (value.isBlank() || !entries.containsKey(value.charAt(0)));
        return value.charAt(0);
    }

    static Character centerMenu(PrintStream out, Scanner in, Map<Character, String> entries) {
        // entries.forEach((ch, desc) -> out.printf("[%c] %s%n", ch, desc));
        StringBuilder stringBuilder = new StringBuilder();
        entries.forEach((ch, desc) -> stringBuilder.append(String.format("[%c] %s%n", ch, desc)));
        out.print(center(stringBuilder.toString()));
        String value;
        do {
            value = in.nextLine();
        } while (value.isBlank() || !entries.containsKey(value.charAt(0)));
        return value.charAt(0);
    }

    /**
     * Sets the state.
     *
     * @param state the next state
     */
    void setState(CliState state) {
        this.state = state;
        state.render(this, System.out, new Scanner(System.in));
    }

    @Override
    public void execute() {
        state.render(this, System.out, new Scanner(System.in));
    }

    public void connect() {
        JsonObject jsonConfig = new Gson().fromJson(new InputStreamReader(Objects.requireNonNull(ClientServerHandler.class.getResourceAsStream(jsonConfigPath))), JsonObject.class);

        String host = jsonConfig.get("host").getAsString();
        int port = jsonConfig.get("port").getAsInt();

        try {
            new ClientServerHandler(host, port).start();
        } catch (IOException e) {
            System.exit(1);
        }
    }
}
