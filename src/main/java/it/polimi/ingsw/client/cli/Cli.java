package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.ClientServerHandler;
import it.polimi.ingsw.client.ReducedObjectPrinter;
import it.polimi.ingsw.client.Ui;
import it.polimi.ingsw.common.events.vcevents.VCEvent;
import it.polimi.ingsw.common.reducedmodel.ReducedGame;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static it.polimi.ingsw.client.cli.CliState.renderMainTitle;

public class Cli implements Ui {
    static final int width = 80;

    /** The current state of the interface. */
    private CliState state;

    private final CliView view;
    private final ReducedGame cache;

    private final Scanner scanner;

    private String nickname; //should be moved out of here

    private BlockingQueue<CliState> stateQueue = new LinkedBlockingDeque<>();

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Cli() {
        this.scanner = new Scanner(System.in);
        
        this.stateQueue.add(new SplashState());

        ReducedObjectPrinter printer = new CliReducedObjectPrinter(this);

        this.cache = new ReducedGame(printer);
        this.view = new CliView(cache, this, printer);
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
    public void setState(CliState state) {
        stateQueue.add(state);
    }

    public void repeatState(String s) {
        System.out.println(s);
        stateQueue.add(this.state);
    }

    @Override
    public void execute() {
        try {
            this.state = stateQueue.take();;
        } catch (InterruptedException e) { e.printStackTrace(); }
        
        while (this.state != null) {
            System.out.println(state.getClass().getSimpleName());
            state.render(this, System.out, scanner, this.cache);
            try {
                this.state = stateQueue.take();
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
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

    public Map<Integer, Map<String, Integer>> promptShelves(PrintStream out, Scanner in) {
        System.out.println("Choose mapping shelf-resource-quantity:");
        final Map<Integer, Map<String, Integer>> shelves = new HashMap<>();
        int container;
        String resource;
        int amount;

        String input = "";
        while (!input.equalsIgnoreCase("Y")) {
            input = Cli.prompt(out, in, "Which container?");
            try {
                container = Integer.parseInt(input);
                int finalContainer = container;
                if(cache.getContainers().stream().filter(c -> c.getId() == finalContainer).findAny().isEmpty()) {
                    System.out.println("You do not own this container. Try again");
                    continue;
                };
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
                    shelves.get(container).replace(resource, shelves.get(container).get(resource) + amount);
                } else {
                    shelves.get(container).put(resource, amount);
                }
            } else {
                Map<String, Integer> resourceMapping = new HashMap<>();
                resourceMapping.put(resource, amount);
                shelves.put(container, resourceMapping);
            }
            input = Cli.prompt(out, in, "Are you done choosing? [Y/*]");
        }
        System.out.println("Building shelves...");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return shelves;
    }

    public Map<String, Integer> promptResources(PrintStream out, Scanner in) {
        String resource;
        int amount;
        String input;
        Map<String, Integer> result = new HashMap<>();

        input = "";
        while (!input.equalsIgnoreCase("Y")) {
            resource = Cli.prompt(out, in, "Which resource do you want as replacement to Zeros/Blanks?");

            input = Cli.prompt(out, in, "How many?");
            try {
                amount = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please input an integer.");
                continue;
            }

            if(result.containsKey(resource)) {
                result.replace(resource, result.get(resource) + amount);
            } else {
                result.put(resource, amount);
            }

            input = Cli.prompt(out, in, "Are you done choosing? [Y/*]");
        }
        return result;
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
