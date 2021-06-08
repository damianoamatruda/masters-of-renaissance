package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.OfflineClient;
import it.polimi.ingsw.client.OnlineClient;
import it.polimi.ingsw.client.cli.components.Resource;
import it.polimi.ingsw.client.cli.components.ResourceContainer;
import it.polimi.ingsw.client.cli.components.ResourceMap;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.EventDispatcher;
import it.polimi.ingsw.common.Network;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

public class Cli extends EventDispatcher {
    static final int width = 179;

    private final View view;
    private Network network;

    /** The current state of the interface. */
    private CliState state;
    private volatile boolean ready;

    private final ViewModel viewModel;

    private final PrintStream out;
    private final Scanner in;

    private InputStream gameConfigStream;
    private boolean offline;

    public Cli() {
        this.view = new View();
        this.view.registerOnVC(this);
        this.registerOnMV(this.view);

        this.network = null;

        this.viewModel = new ViewModel();
        this.out = System.out;
        this.in = new Scanner(System.in);

        this.gameConfigStream = null;
        this.offline = false;
    }

    public static void main(String[] args) {
        new Cli().start(); // new Thread(this::start).start();
    }

    public static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static String centerLine(String str, int width) {
        int marginLeft = (width - textLength(str)) / 2;
        int marginRight = width - marginLeft - textLength(str);

        if (textLength(str) > width) {
            System.err.println("String too long:");
            System.err.println(str);
            System.err.printf("Max length: %d. Got: %d.", width, textLength(str));
            throw new IllegalArgumentException();
        }

        StringBuilder stringBuilder = new StringBuilder();
        str.lines().forEachOrdered(line -> stringBuilder.append(" ".repeat(marginLeft)).append(line).append(" ".repeat(marginRight)));
        return stringBuilder.toString();
    }

    public static String center(String str) {
        if (str.lines().count() == 0)
            return "";

        int maxLineWidth = str.lines().mapToInt(Cli::textLength).max().orElseThrow();

        if (maxLineWidth > width) {
            System.err.println("String too long:");
            System.err.println(str.lines().filter(s -> s.length() == maxLineWidth).findAny().orElseThrow());
            System.err.printf("Max length: %d. Got: %d.", width, textLength(str.lines().filter(s -> s.length() == maxLineWidth).findAny().orElseThrow()));
            throw new IllegalArgumentException();
        }

        int marginLeft = (width - maxLineWidth) / 2;

        StringBuilder stringBuilder = new StringBuilder();
        str.lines().forEachOrdered(line -> stringBuilder.append(" ".repeat(marginLeft)).append(line).append(" ".repeat(width - marginLeft - textLength(line))).append("\n"));
        return stringBuilder.toString();
    }

    public static String left(String str, int width, char fill) {
        if (textLength(str) > width) {
            System.err.println("String too long:");
            System.err.println(str);
            System.err.printf("Max length: %d. Got: %d.", width, textLength(str));
            throw new IllegalArgumentException();
        }
        return str + Character.toString(fill).repeat(width - textLength(str));
    }

    public static String left(String str, int width) {
        return left(str, width, ' ');
    }

    public static String right(String str, int width, char fill) {
        return Character.toString(fill).repeat(width - textLength(str)) + str;
    }

    public static String right(String str, int width) {
        return right(str, width, ' ');
    }

    public static int textLength(String str) {
        return str.replaceAll("\u001B\\[[;\\d]*m", "").length();
    }

    public static String slimLine(int width) {
        return "─".repeat(width) + "\n";
    }

    public static String slimLine() {
        return slimLine(width);
    }

    /* public static String spaced(String str) {
        return String.format("%n%s%n", str);
    } */

    public void start() {
        setState(new SplashState());
        renderNextState();
    }

    public void stop() {
        stopNetwork();
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
        view.addEventListener(UpdateActivateLeader.class, event -> state.on(this, event));
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
        view.removeEventListener(UpdateActivateLeader.class, event -> state.on(this, event));
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
        if (input.equalsIgnoreCase("q") && !(viewModel.getLocalPlayerNickname().equals(""))) {
            dispatch(new ReqQuit());
            return null;
        }
        return input;
    }

    public int promptInt(String prompt) {
        int value;
        while (true) {
            String input = prompt(prompt);
            try {
                value = Integer.parseInt(input);
                break;
            } catch (NumberFormatException ignored) {
            }
        }
        return value;
    }

    public File promptFile(String prompt) {
        String path = prompt(prompt);
        return new File(path);
    }

    public PrintStream getOut() {
        return out;
    }

    public Scanner getIn() {
        return in;
    }

    public ViewModel getViewModel() {
        return viewModel;
    }

    /**
     * Sets the state.
     *
     * @param state the next state
     */
    synchronized void setState(CliState state) {
        this.state = state;
        this.ready = true;
        notifyAll();
    }

    /**
     * Sets the current state based on the next requested state.
     * If no state is requested, waits for a request.
     * While a state is being rendered, requested states aren't applied:
     * only the last requested state is applied, and only as soon as the current state has finished rendering.
     */
    synchronized void renderNextState() {
        while (true) {
            while (!ready) {
                try {
                    wait();
                } catch (InterruptedException e) { }
            }
            ready = false;
            
            out.println();
            clear();
            out.println(center(String.format("\u001b[31m%s\u001B[0m", state.getClass().getSimpleName())));
            state.render(this);
        }
    }

    void clear() {
        out.print("\033[H\033[2J");
        out.flush();
    }

    void pause() {
        in.nextLine();
    }

    void promptPause() {
        out.print(Cli.center("[Press ENTER to continue]"));
        pause();
    }

    public void trackSlimLine() {
        out.print(slimLine());
    }

    void repeatState(String str) {
        out.println(str);
        promptPause();
        setState(this.state);
    }

    void startOfflineClient() {
        stopNetwork();
        network = new OfflineClient(view, gameConfigStream);
        try {
            network.start();
        } catch (IOException ignored) {
        }
        offline = true;
    }

    void startOnlineClient(String host, int port) throws IOException {
        stopNetwork();
        network = new OnlineClient(view, host, port);
        network.start();
        offline = false;
    }

    void stopNetwork() {
        if (network != null) {
            network.stop();
            network = null;
        }
    }

    // TODO: Maybe make it a component, like Menu
    Map<Integer, Map<String, Integer>> promptShelves(Map<String, Integer> resMap, Set<Integer> allowedShelves) {
        Map<Integer, Map<String, Integer>> shelves = new HashMap<>();

        showShelves(this.getViewModel().getLocalPlayerNickname());

        Map<String, Integer> remainingResMap = new HashMap<>(resMap);
        int totalQuantity = remainingResMap.values().stream().mapToInt(Integer::intValue).sum();
        int allocQuantity = 0;
        while (allocQuantity < totalQuantity) {
            out.println();
            out.println("Remaining:");
            new ResourceMap(remainingResMap).render(this);

            out.println();
            out.println("You can finish at any time by pressing B.");

            out.println();

            String res = promptResource(remainingResMap.keySet());
            if (res == null)
                return shelves;

            int quantity = promptQuantity(remainingResMap.get(res));
            if (quantity < 0)
                return shelves;

            int shelfId = promptShelfId(allowedShelves);
            if (shelfId < 0)
                return shelves;

            // TODO: Check for shelf overshooting
            shelves.compute(shelfId, (sid, rMap) -> {
                if (rMap == null)
                    rMap = new HashMap<>();
                rMap.compute(res, (k, v) -> v == null ? quantity : quantity + v);
                return rMap;
            });

            allocQuantity += quantity;
            remainingResMap.computeIfPresent(res, (k, v) -> v - quantity);
        }

        return shelves;
    }

    // TODO: Maybe make it a component, like Menu
    Map<String, Integer> promptResources(Set<String> allowedResources, int totalQuantity) {
        Map<String, Integer> replacedRes = new HashMap<>();

        out.println("Resources you can choose:");

        out.println();
        allowedResources.forEach(r -> {
            new Resource(r).render(this);
            out.println();
        });

        int allocQuantity = 0;
        while (allocQuantity < totalQuantity) {
            out.println();
            out.println("You can finish at any time by pressing B.");

            out.println();

            String res = promptResource(allowedResources);
            if (res == null)
                return replacedRes;

            int quantity = promptQuantity(totalQuantity - allocQuantity);
            if (quantity < 0)
                return replacedRes;

            allocQuantity += quantity;
            replacedRes.put(res, quantity);
        }

        return replacedRes;
    }

    Map<Integer, Map<String, Integer>> promptShelvesSetup(Set<String> allowedResources, int totalQuantity, Set<Integer> allowedShelves) {
        Map<Integer, Map<String, Integer>> shelves = new HashMap<>();

        showShelves(this.getViewModel().getLocalPlayerNickname());

        int allocQuantity = 0;
        while (allocQuantity < totalQuantity) {
            out.println();
            out.println("Resources you can choose:");

            out.println();
            allowedResources.forEach(r -> {
                new Resource(r).render(this);
                out.println();
            });

            out.println();

            out.println("You can finish at any time by pressing B.");
            out.println();

            String res = promptResource(allowedResources);
            if (res == null)
                return shelves;

            int quantity = promptQuantity(totalQuantity - allocQuantity);
            if (quantity < 0)
                return shelves;

            int shelfId = promptShelfId(allowedShelves);
            if (shelfId < 0)
                return shelves;

            // TODO: Check for shelf overshooting
            shelves.compute(shelfId, (sid, rMap) -> {
                if (rMap == null)
                    rMap = new HashMap<>();
                rMap.compute(res, (k, v) -> v == null ? quantity : quantity + v);
                return rMap;
            });

            allocQuantity += quantity;
        }

        return shelves;
    }

    private String promptResource(Set<String> resTypes) {
        Optional<String> resType;
        do {
            String input = prompt("Resource");
            if (input.equalsIgnoreCase("B"))
                return null;
            resType = resTypes.stream().filter(r -> r.equalsIgnoreCase(input)).findAny();
        } while (resType.isEmpty());
        return resType.get();
    }

    private int promptQuantity(int maxQuantity) {
        int quantity = -1;
        String input;
        while (quantity < 1) {
            input = prompt("Quantity");
            if (input.equalsIgnoreCase("B"))
                return -1;
            try {
                quantity = Integer.parseInt(input);
                if (quantity > maxQuantity)
                    quantity = -1;
            } catch (NumberFormatException ignored) {
            }
        }
        return quantity;
    }

    private int promptShelfId(Set<Integer> allowedShelves) {
        int shelfId = -1;
        String input;
        while (shelfId < 0) {
            input = prompt("Shelf");
            if (input.equalsIgnoreCase("B"))
                return -1;
            try {
                shelfId = Integer.parseInt(input);
                if (!allowedShelves.contains(shelfId))
                    shelfId = -1;
            } catch (NumberFormatException ignored) {
            }
        }
        return shelfId;
    }

    @Deprecated
    void showShelves(String player) {
        StringBuilder stringBuilder = new StringBuilder();

        if (viewModel.getPlayerWarehouseShelves(player).size() > 0) {
            stringBuilder.append(String.format("%s's warehouse shelves:", player)).append("\n");
            viewModel.getPlayerWarehouseShelves(player).forEach(c ->
                    stringBuilder.append("\n").append(new ResourceContainer(c).getString(this)));
        }
        if (viewModel.getPlayerDepots(player).size() > 0) {
            stringBuilder.append("\n");
            stringBuilder.append(String.format("%s's available leader depots:%n", player));
            viewModel.getPlayerDepots(player).forEach(c ->
                    stringBuilder.append("\n").append(new ResourceContainer(c).getString(this)));
        }

        out.println(Cli.center(stringBuilder.toString()));
    }

    @Deprecated
    void showStrongbox(String player) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("%s's strongbox:", player)).append("\n");
        viewModel.getContainer(viewModel.getPlayerData(player).orElseThrow().getStrongbox()).ifPresent(c ->
                stringBuilder.append("\n").append(new ResourceContainer(c).getString(this)));

        out.println(Cli.center(stringBuilder.toString()));
    }

    @Deprecated
    public void showContainers(String player) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("%s's containers:", player)).append("\n");
        viewModel.getPlayerShelves(player).forEach(c ->
                stringBuilder.append("\n").append(new ResourceContainer(c).getString(this)));

        out.println(Cli.center(stringBuilder.toString()));
    }

    void quit() {
        clear();
        stop();
    }

    Optional<InputStream> getGameConfigStream() {
        return Optional.ofNullable(gameConfigStream);
    }

    void setGameConfigStream(InputStream gameConfigStream) {
        this.gameConfigStream = gameConfigStream;
    }

    boolean isOffline() {
        return offline;
    }
}
