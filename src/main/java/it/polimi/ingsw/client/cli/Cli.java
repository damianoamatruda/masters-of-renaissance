package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.ClientServerHandler;
import it.polimi.ingsw.client.Ui;
import it.polimi.ingsw.common.events.vcevents.VCEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Cli implements Ui {
    static final int width = 80;

    /** The current state of the interface. */
    private CliState state;

    private CliView view;

    public Cli() {
        this.state = new SplashState();
        this.view = new CliView();
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

    static String prompt(PrintStream out, Scanner in, String prompt) {
        out.printf("%s: ", prompt);
        return in.nextLine();
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

    void startServerHandler(String host, int port) {
        try {
            new ClientServerHandler(host, port, view).start();
        } catch (IOException e) {
            System.exit(1);
        }
    }

    public void sendToView(VCEvent event) {
        view.on(event);
    }
}
