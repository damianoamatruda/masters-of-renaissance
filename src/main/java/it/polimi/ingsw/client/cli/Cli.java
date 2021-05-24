package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.LocalClient;
import it.polimi.ingsw.client.NetworkClient;
import it.polimi.ingsw.client.ReducedObjectPrinter;
import it.polimi.ingsw.common.EventDispatcher;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;
import it.polimi.ingsw.common.reducedmodel.ReducedGame;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Cli extends EventDispatcher {
    static final int width = 160;

    private final View view;

    /** The current state of the interface. */
    private CliState state;

    private final ReducedGame cache;
    private final ReducedObjectPrinter printer;

    private final PrintStream out;
    private final Scanner in;

    private final BlockingQueue<CliState> stateQueue;

    private volatile boolean running;

    private boolean singleplayer;

    public Cli() {
        this.view = new View();
        this.view.registerOnVC(this);
        this.registerOnMV(this.view);

        this.stateQueue = new LinkedBlockingDeque<>();
        this.stateQueue.add(new SplashState());

        this.cache = new ReducedGame();
        this.printer = new CliReducedObjectPrinter(this, cache);
        this.cache.setPrinter(printer);
        this.out = System.out;
        this.in = new Scanner(System.in);

        this.running = false;
        this.singleplayer = false;
    }

    public static void main(String[] args) {
        new Cli().start(); // new Thread(this::start).start();
    }

    public static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static String centerLine(String s, int width) {
        int marginLeft = (width - s.length()) / 2;

        StringBuilder stringBuilder = new StringBuilder();
        s.lines().forEachOrdered(line -> stringBuilder.append(" ".repeat(marginLeft)).append(line));
        return stringBuilder.toString();
    }

    public static String center(String s) {
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

    public void start() {
        running = true;
        while (running) {
            try {
                state = stateQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                running = false;
                break;
            }

            out.println();
            for (int i = 0; i < width; i++)
                out.print("═");
            out.println();
            out.println("\u001b[31m" + Cli.center(state.getClass().getSimpleName()) + "\u001B[0m");
            state.render(this);
        }
    }

    public void stop() {
        running = false;
    }

    public void registerOnMV(EventDispatcher view) {
        view.addEventListener(ResQuit.class, event -> state.on(this, event));
        view.addEventListener(UpdateBookedSeats.class, event -> state.on(this, event));
        view.addEventListener(UpdateJoinGame.class, event -> state.on(this, event));
        view.addEventListener(ErrNewGame.class, event -> state.on(this, event));
        view.addEventListener(ErrNickname.class, event -> state.on(this, event));
        view.addEventListener(ErrAction.class, event -> state.on(this, event));
        view.addEventListener(ErrActiveLeaderDiscarded.class, event -> state.on(this, event));
        view.addEventListener(ErrBuyDevCard.class, event -> state.on(this, event));
        view.addEventListener(ErrCardRequirements.class, event -> state.on(this, event));
        view.addEventListener(ErrInitialChoice.class, event -> state.on(this, event));
        view.addEventListener(ErrNoSuchEntity.class, event -> state.on(this, event));
        view.addEventListener(ErrObjectNotOwned.class, event -> state.on(this, event));
        view.addEventListener(ErrReplacedTransRecipe.class, event -> state.on(this, event));
        view.addEventListener(ErrResourceReplacement.class, event -> state.on(this, event));
        view.addEventListener(ErrResourceTransfer.class, event -> state.on(this, event));
        view.addEventListener(UpdateAction.class, event -> state.on(this, event));
        view.addEventListener(UpdateActionToken.class, event -> state.on(this, event));
        view.addEventListener(UpdateCurrentPlayer.class, event -> state.on(this, event));
        view.addEventListener(UpdateDevCardGrid.class, event -> state.on(this, event));
        view.addEventListener(UpdateDevCardSlot.class, event -> state.on(this, event));
        view.addEventListener(UpdateFaithPoints.class, event -> state.on(this, event));
        view.addEventListener(UpdateGame.class, event -> state.on(this, event));
        view.addEventListener(UpdateGameEnd.class, event -> state.on(this, event));
        view.addEventListener(UpdateLastRound.class, event -> state.on(this, event));
        view.addEventListener(UpdateLeader.class, event -> state.on(this, event));
        view.addEventListener(UpdateLeadersHandCount.class, event -> state.on(this, event));
        view.addEventListener(UpdateMarket.class, event -> state.on(this, event));
        view.addEventListener(UpdatePlayer.class, event -> state.on(this, event));
        view.addEventListener(UpdatePlayerStatus.class, event -> state.on(this, event));
        view.addEventListener(UpdateResourceContainer.class, event -> state.on(this, event));
        view.addEventListener(UpdateSetupDone.class, event -> state.on(this, event));
        view.addEventListener(UpdateVaticanSection.class, event -> state.on(this, event));
        view.addEventListener(UpdateVictoryPoints.class, event -> state.on(this, event));
        view.addEventListener(UpdateLeadersHand.class, event -> state.on(this, event));
    }

    public void unregisterOnMV(EventDispatcher view) {
        view.removeEventListener(ResQuit.class, event -> state.on(this, event));
        view.removeEventListener(UpdateBookedSeats.class, event -> state.on(this, event));
        view.removeEventListener(UpdateJoinGame.class, event -> state.on(this, event));
        view.removeEventListener(ErrNewGame.class, event -> state.on(this, event));
        view.removeEventListener(ErrNickname.class, event -> state.on(this, event));
        view.removeEventListener(ErrAction.class, event -> state.on(this, event));
        view.removeEventListener(ErrActiveLeaderDiscarded.class, event -> state.on(this, event));
        view.removeEventListener(ErrBuyDevCard.class, event -> state.on(this, event));
        view.removeEventListener(ErrCardRequirements.class, event -> state.on(this, event));
        view.removeEventListener(ErrInitialChoice.class, event -> state.on(this, event));
        view.removeEventListener(ErrNoSuchEntity.class, event -> state.on(this, event));
        view.removeEventListener(ErrObjectNotOwned.class, event -> state.on(this, event));
        view.removeEventListener(ErrReplacedTransRecipe.class, event -> state.on(this, event));
        view.removeEventListener(ErrResourceReplacement.class, event -> state.on(this, event));
        view.removeEventListener(ErrResourceTransfer.class, event -> state.on(this, event));
        view.removeEventListener(UpdateAction.class, event -> state.on(this, event));
        view.removeEventListener(UpdateActionToken.class, event -> state.on(this, event));
        view.removeEventListener(UpdateCurrentPlayer.class, event -> state.on(this, event));
        view.removeEventListener(UpdateDevCardGrid.class, event -> state.on(this, event));
        view.removeEventListener(UpdateDevCardSlot.class, event -> state.on(this, event));
        view.removeEventListener(UpdateFaithPoints.class, event -> state.on(this, event));
        view.removeEventListener(UpdateGame.class, event -> state.on(this, event));
        view.removeEventListener(UpdateGameEnd.class, event -> state.on(this, event));
        view.removeEventListener(UpdateLastRound.class, event -> state.on(this, event));
        view.removeEventListener(UpdateLeader.class, event -> state.on(this, event));
        view.removeEventListener(UpdateLeadersHandCount.class, event -> state.on(this, event));
        view.removeEventListener(UpdateMarket.class, event -> state.on(this, event));
        view.removeEventListener(UpdatePlayer.class, event -> state.on(this, event));
        view.removeEventListener(UpdatePlayerStatus.class, event -> state.on(this, event));
        view.removeEventListener(UpdateResourceContainer.class, event -> state.on(this, event));
        view.removeEventListener(UpdateSetupDone.class, event -> state.on(this, event));
        view.removeEventListener(UpdateVaticanSection.class, event -> state.on(this, event));
        view.removeEventListener(UpdateVictoryPoints.class, event -> state.on(this, event));
        view.removeEventListener(UpdateLeadersHand.class, event -> state.on(this, event));
    }

    public String prompt(String prompt, String defaultValue) {
        out.printf("%s (default: %s): ", prompt, defaultValue);
        String value = in.nextLine();
        return !value.isBlank() ? value : defaultValue;
    }

    public String prompt(String prompt) {
        if (!prompt.isEmpty())
            out.printf("%s: ", prompt);
        String input = in.nextLine();
        if (input.toUpperCase().startsWith("Q"))
            dispatch(new ReqQuit());
        return input;
    }

    public PrintStream getOut() {
        return out;
    }

    public Scanner getIn() {
        return in;
    }

    public ReducedObjectPrinter getPrinter() {
        return printer;
    }

    public ReducedGame getCache() {
        return cache;
    }

    @Deprecated
    CliState getState() {
        return state;
    }

    /**
     * Sets the state.
     *
     * @param state the next state
     */
    void setState(CliState state) {
        stateQueue.add(state);
    }

    void clear() {
        out.print("\033[H\033[2J");
        out.flush();
    }

    void pause() {
        in.nextLine();
    }

    void trackSlimLine() {
        for (int i = 0; i < width; i++)
            out.print("─");
        out.println();
    }

    void repeatState(String s) {
        out.println(s);
        stateQueue.add(this.state);
    }

    void startNetworkClient(String host, int port) throws IOException {
        new NetworkClient(view, host, port).start();
        singleplayer = false;
    }

    void startLocalClient() {
        new LocalClient(view).start();
        singleplayer = true;
    }

    // TODO: Move this out of this class
    Map<Integer, Map<String, Integer>> promptShelves() {
        out.println("Choose mapping shelf-resource-quantity:");
        final Map<Integer, Map<String, Integer>> shelves = new HashMap<>();
        int container;
        String resource;
        int amount;

        String input = "";
        while (!input.equalsIgnoreCase("Y")) {
            input = prompt("Which container? (Input an ID, or else Enter to skip)");
            if (input.isEmpty())
                break;

            try {
                container = Integer.parseInt(input);
//                int finalContainer = container;
//                if(cache.getContainers().stream().filter(c -> c.getId() == finalContainer).findAny().isEmpty()) {
//                    out.println("You do not own this container. Try again");
//                    continue;
//                };
            } catch (NumberFormatException e) {
                out.println("Please input an integer.");
                continue;
            }

            resource = prompt("Which resource?");

            input = prompt("How many?");
            try {
                amount = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                out.println("Please input an integer.");
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
            input = prompt("Are you done choosing? [Y/*]");
        }
        out.println("Building shelves...");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return shelves;
    }

    // TODO: Move this out of this class
    Map<String, Integer> promptResources() {
        out.println("Choosing blank resource replacements:");

        String resource;
        int amount;
        String input;
        Map<String, Integer> result = new HashMap<>();

        input = "";
        while (!input.equalsIgnoreCase("Y")) {
            resource = prompt("Which resource do you want as replacement to Zeros/Blanks? (Enter to skip)");
            if (resource.isEmpty())
                break;

            input = prompt("How many blanks would you like to replace?");
            try {
                amount = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                out.println("Please input an integer.");
                continue;
            }

            if(result.containsKey(resource)) {
                result.replace(resource, result.get(resource) + amount);
            } else {
                result.put(resource, amount);
            }

            input = prompt("Are you done choosing? [Y/*]");
        }
        return result;
    }

    void quit() {
        clear();
        stop();
    }

    boolean isSingleplayer() {
        return singleplayer;
    }
}
