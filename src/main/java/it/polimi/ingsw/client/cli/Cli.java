package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.OfflineClient;
import it.polimi.ingsw.client.OnlineClient;
import it.polimi.ingsw.client.ReducedObjectPrinter;
import it.polimi.ingsw.client.ViewModel.ViewModel;
import it.polimi.ingsw.common.EventDispatcher;
import it.polimi.ingsw.common.Network;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Cli extends EventDispatcher {
    static final int width = 160;

    private final View view;
    private Network network;

    /** The current state of the interface. */
    private CliState state;

    private final ViewModel viewModel;
    private final ReducedObjectPrinter printer;

    private final PrintStream out;
    private final Scanner in;

    private final BlockingQueue<CliState> stateQueue;

    private volatile boolean running;

    private boolean offline;

    public Cli() {
        this.view = new View();
        this.view.registerOnVC(this);
        this.registerOnMV(this.view);

        this.network = null;

        this.stateQueue = new LinkedBlockingDeque<>();
        this.stateQueue.add(new SplashState());

        this.viewModel = new ViewModel();
        this.printer = new CliReducedObjectPrinter(this, viewModel);
        this.out = System.out;
        this.in = new Scanner(System.in);

        this.running = false;
        this.offline = false;
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
        if (network != null)
            network.stop();
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

    public ViewModel getViewModel() {
        return viewModel;
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
        while (stateQueue.size() > 0) {
            try {
                stateQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
        setState(this.state);
    }

    void startOfflineClient() {
        network = new OfflineClient(view);
        try {
            network.start();
        } catch (IOException ignored) {
        }
        offline = true;
    }

    void startOnlineClient(String host, int port) throws IOException {
        network = new OnlineClient(view, host, port);
        network.start();
        offline = false;
    }

    Map<String, Integer> promptResources(int replaceable, List<String> allowedReplacements) {
        Map<String, Integer> replacedRes = new HashMap<>();
        
        this.getOut().println("These are the resources you can replace blanks with:");
        allowedReplacements.forEach(n -> this.getOut().println(n));

        String input = "";
        while (replaceable > 0) {
            int count = 0; String res = "";

            while (!allowedReplacements.contains(res)) {
                res = this.prompt("Replacement resource (B to go back)");
                if (res.equalsIgnoreCase("B"))
                    break;
            }
            if (res.equalsIgnoreCase("B"))
                break;

            while (count < 1) {
                input = this.prompt("Amount replaced (> 0, B to go back):");
            
                if (input.equalsIgnoreCase("B"))
                    break;
                try {
                    count = Integer.parseInt(input);
                    if (replaceable - count < 0) {
                        this.getOut().println(String.format("Too many chosen: %d available", replaceable));
                        count = 0;
                    }
                } catch (Exception e) { }
            }
            if (input.equalsIgnoreCase("B"))
                break;

            replacedRes.put(res, count);
            replaceable -= count;
        }

        return replacedRes;
    }

    Map<Integer, Map<String, Integer>> promptShelves(Map<String, Integer> totalRes) {
        Map<Integer, Map<String, Integer>> shelves = new HashMap<>();

        this.getPrinter().showWarehouseShelves(this.getViewModel().getLocalPlayerNickname());
        this.getOut().println("These are your shelves. You can finish at any time by pressing B.");

        // prompt user for resource -> count -> shelf to put it in
        int totalResCount = totalRes.entrySet().stream().mapToInt(e -> e.getValue().intValue()).sum(),
            allocResCount = 0;
        while (allocResCount < totalResCount) { // does not check for overshooting
            this.getOut().println("Remaining:");
            totalRes.entrySet().forEach(e -> this.getOut().println(String.format("%s: %d", e.getKey(), e.getValue())));
            
            int count = 0, shelfID = -1; String res = "";
            while (!totalRes.keySet().contains(res)) {
                res = this.prompt("Resource");

                res = res.substring(0, 1).toUpperCase() + res.substring(1);
                if (res.equalsIgnoreCase("B"))
                    break;
            }
            if (res.equalsIgnoreCase("B"))
                break;

            String input = "";
            while (count < 1) {
                input = this.prompt("Amount (> 0)");
                
                if (input.equalsIgnoreCase("B"))
                    break;
                try {
                    count = Integer.parseInt(input);
                    if (count > totalRes.get(res))
                        count = 0;
                } catch (Exception e) { }
            }
            if (input.equalsIgnoreCase("B"))
                break;

            while (shelfID < 0) {
                input = this.prompt("Shelf ID");

                if (input.equalsIgnoreCase("B"))
                    break;
                try {
                    shelfID = Integer.parseInt(input);
                } catch (Exception e) { }
            }
            if (input.equalsIgnoreCase("B"))
                break;
            
            String r = res; int c = count; // rMap.put below complained they weren't final
            shelves.compute(shelfID, (sid, rMap) -> {
                if (rMap == null)
                    rMap = new HashMap<>();

                rMap.compute(r, (k, v) -> v == null ? c : c + v);
                return rMap;
            });

            allocResCount += count;
            totalRes.compute(res, (k, v) -> v - c);
        }

        return shelves;
    }

    void quit() {
        stop();
        clear();
    }

    boolean isOffline() {
        return offline;
    }
}
