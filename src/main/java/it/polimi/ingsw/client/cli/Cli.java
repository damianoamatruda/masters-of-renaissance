package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.ClientServerHandler;
import it.polimi.ingsw.client.Ui;
import it.polimi.ingsw.common.events.vcevents.VCEvent;
import it.polimi.ingsw.common.reducedmodel.ReducedGame;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static it.polimi.ingsw.client.cli.CliState.renderMainTitle;

public class Cli implements Ui {
    static final int width = 80;

    /** The current state of the interface. */
    private CliState state;

    private CliView view;
    private ReducedGame model;

    private Scanner scanner;

    private String nickname; //should be moved out of here

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Cli() {
        this.scanner = new Scanner(System.in);
        
        this.state = new SplashState();

        this.model = new ReducedGame();
        this.view = new CliView(model, this);
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
        state.render(this, System.out, scanner);
    }

    @Override
    public void execute() {
        state.render(this, System.out, scanner);
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

    public void repeatState(String s) {
        System.out.println(s);
        state.render(this, System.out, scanner);
    }

    public Map<Integer, Map<String, Integer>> promptShelves(PrintStream out, Scanner in) {
        final Map<Integer, Map<String, Integer>> shelves = new HashMap<>();
        int container;
        String resource;
        int amount;

        String input = "";
        while (!input.equalsIgnoreCase("Y")) {
            input = Cli.prompt(out, in, "Which container?");
            try {
                container = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please input an integer.");
                continue;
            }

            resource = Cli.prompt(out, in, "Which resource?");

            input = Cli.prompt(out, in, "How many?");
            try {
                amount = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please input an integer.");
                continue;
            }

            if(shelves.containsKey(container)) {
                if(shelves.get(container).containsKey(resource)) {
                    shelves.get(container).replace(resource, shelves.get(container).get(resource) + amount);  //bugged (it loops forever?)
                } else {
                    shelves.get(container).put(resource, amount);
                }
            } else {
                shelves.put(container, Map.of(resource, amount));
            }
            input = Cli.prompt(out, in, "Are you done choosing what to pay? [Y/*]");
        }
        System.out.println("Building shelves...");
        return shelves;
    }

    public void quit() {
        PrintStream out = System.out;

        Cli.clear(out);
        renderMainTitle(out);
        for (int i = 0; i < 2; i++)
            out.println();
        out.print("Quitting in 2 seconds...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        Cli.clear(out);
        renderMainTitle(out);
        for (int i = 0; i < 2; i++)
            out.println();
        out.print("Quitting in 1 seconds...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        Cli.clear(out);
    }
}
