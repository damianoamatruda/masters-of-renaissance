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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Cli extends EventDispatcher {
    private static final int width = 179;
    private static final String backValue = " ";

    private volatile boolean running;
    private volatile boolean ready;

    private final View view;
    private Network network;

    /** The current state of the interface. */
    private CliState state;

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
        return slimLineNoNewLine(width) + "\n";
    }

    public static String slimLineNoNewLine(int width) {
        return "─".repeat(width);
    }

    public static String slimLine() {
        return slimLine(width);
    }

    /* public static String spaced(String str) {
        return String.format("%n%s%n", str);
    } */

    public synchronized void start() {
        setState(new SplashState());

        /*
         * Sets the current state based on the next requested state.
         * If no state is requested, waits for a request.
         * While a state is being rendered, requested states aren't applied:
         * only the last requested state is applied, and only as soon as the current state has finished rendering.
         */
        running = true;
        while (running) {
            while (!ready) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    ready = true;
                }
            }
            ready = false;

            // out.println();
            clear();
            // out.println(center(String.format("\u001b[31m%s\u001B[0m", state.getClass().getSimpleName())));
            state.render(this);
        }
    }

    public void stop() {
        running = false;
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

    public Optional<String> prompt(String prompt, String defaultValue) {
        if (prompt.isEmpty())
            throw new IllegalArgumentException("Prompt cannot be empty.");
        out.printf("%s (default: %s): ", prompt, defaultValue);
        String value = in.nextLine();
        if (value.equals(backValue))
            return Optional.empty();
        return Optional.of(!value.isBlank() ? value : defaultValue);
    }

    public Optional<String> prompt(String prompt) {
        if (!prompt.isEmpty())
            out.printf("%s: ", prompt);
        String value = in.nextLine();
        if (value.equals(backValue))
            return Optional.empty();
        return Optional.of(value);
    }

    public Optional<Integer> promptInt(String prompt) {
        int value;
        while (true) {
            Optional<String> input = prompt(prompt);
            if (input.isEmpty())
                return Optional.empty();
            try {
                value = Integer.parseInt(input.get());
                break;
            } catch (NumberFormatException ignored) {
            }
        }
        return Optional.of(value);
    }

    public Optional<File> promptFile(String prompt) {
        Optional<String> path = prompt(prompt);
        if (path.isEmpty())
            return Optional.empty();
        return Optional.of(new File(path.get()));
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
    Optional<Map<Integer, Map<String, Integer>>> promptShelves(Map<String, Integer> resMap, Set<Integer> allowedShelves) {
        Map<Integer, Map<String, Integer>> shelves = new HashMap<>();

        showShelves(this.getViewModel().getLocalPlayerNickname());

        Map<String, Integer> remainingResMap = new HashMap<>(resMap);
        int totalQuantity = remainingResMap.values().stream().mapToInt(Integer::intValue).sum();
        AtomicInteger allocQuantity = new AtomicInteger();
        while (allocQuantity.get() < totalQuantity) {
            out.println();
            out.println("Remaining:");
            new ResourceMap(remainingResMap).render(this);

            out.println();

            AtomicBoolean valid = new AtomicBoolean(true);

            promptResource(remainingResMap.keySet()).ifPresentOrElse(res -> {
                promptQuantity(remainingResMap.get(res)).ifPresentOrElse(quantity -> {
                    promptShelfId(allowedShelves).ifPresentOrElse(shelfId -> {
                        // TODO: Check for shelf overshooting
                        shelves.compute(shelfId, (sid, rMap) -> {
                            if (rMap == null)
                                rMap = new HashMap<>();
                            rMap.compute(res, (k, v) -> v == null ? quantity : quantity + v);
                            return rMap;
                        });

                        allocQuantity.addAndGet(quantity);
                        remainingResMap.computeIfPresent(res, (k, v) -> v - quantity);
                    }, () -> valid.set(false));
                }, () -> valid.set(false));
            }, () -> valid.set(false));

            if (!valid.get()) {
                // TODO: Take only one step back
                return Optional.empty();
            }
        }

        return Optional.of(shelves);
    }

    // TODO: Maybe make it a component, like Menu
    Optional<Map<String, Integer>> promptResources(Set<String> allowedResources, int totalQuantity) {
        Map<String, Integer> replacedRes = new HashMap<>();

        out.println("Resources you can choose:");

        out.println();
        allowedResources.forEach(r -> {
            new Resource(r).render(this);
            out.println();
        });

        AtomicInteger allocQuantity = new AtomicInteger();
        while (allocQuantity.get() < totalQuantity) {
            out.println();

            AtomicBoolean valid = new AtomicBoolean(true);

            promptResource(allowedResources).ifPresentOrElse(res -> {
                promptQuantity(totalQuantity - allocQuantity.get()).ifPresentOrElse(quantity -> {
                    allocQuantity.addAndGet(quantity);
                    replacedRes.put(res, quantity);
                }, () -> valid.set(false));
            }, () -> valid.set(false));

            if (!valid.get()) {
                // TODO: Take only one step back
                return Optional.empty();
            }
        }

        return Optional.of(replacedRes);
    }

    Optional<Map<Integer, Map<String, Integer>>> promptShelvesSetup(Set<String> allowedResources, int totalQuantity, Set<Integer> allowedShelves) {
        Map<Integer, Map<String, Integer>> shelves = new HashMap<>();

        showShelves(this.getViewModel().getLocalPlayerNickname());

        AtomicInteger allocQuantity = new AtomicInteger();
        while (allocQuantity.get() < totalQuantity) {
            out.println();
            out.println("Resources you can choose:");

            out.println();
            allowedResources.forEach(r -> {
                new Resource(r).render(this);
                out.println();
            });

            out.println();

            AtomicBoolean valid = new AtomicBoolean(true);
            promptResource(allowedResources).ifPresentOrElse(res -> {
                promptQuantity(totalQuantity - allocQuantity.get()).ifPresentOrElse(quantity -> {
                    promptShelfId(allowedShelves).ifPresentOrElse(shelfId -> {
                        // TODO: Check for shelf overshooting
                        shelves.compute(shelfId, (sid, rMap) -> {
                            if (rMap == null)
                                rMap = new HashMap<>();
                            rMap.compute(res, (k, v) -> v == null ? quantity : quantity + v);
                            return rMap;
                        });

                        allocQuantity.addAndGet(quantity);
                    }, () -> valid.set(false));
                }, () -> valid.set(false));
            }, () -> valid.set(false));

            if (!valid.get()) {
                // TODO: Take only one step back
                return Optional.empty();
            }
        }

        return Optional.of(shelves);
    }

    private Optional<String> promptResource(Set<String> resTypes) {
        Optional<String> resType;
        do {
            Optional<String> input = prompt("Resource");
            if (input.isEmpty())
                return Optional.empty();
            resType = resTypes.stream().filter(r -> r.equalsIgnoreCase(input.get())).findAny();
        } while (resType.isEmpty());
        return resType;
    }

    private Optional<Integer> promptQuantity(int maxQuantity) {
        int quantity = -1;
        Optional<String> input;
        while (quantity < 1) {
            input = prompt("Quantity");
            if (input.isEmpty())
                return Optional.empty();
            try {
                quantity = Integer.parseInt(input.get());
                if (quantity > maxQuantity)
                    quantity = -1;
            } catch (NumberFormatException ignored) {
            }
        }
        return Optional.of(quantity);
    }

    private Optional<Integer> promptShelfId(Set<Integer> allowedShelves) {
        int shelfId = -1;
        Optional<String> input;
        while (shelfId < 0) {
            input = prompt("Shelf");
            if (input.isEmpty())
                return Optional.empty();
            try {
                shelfId = Integer.parseInt(input.get());
                if (!allowedShelves.contains(shelfId))
                    shelfId = -1;
            } catch (NumberFormatException ignored) {
            }
        }
        return Optional.of(shelfId);
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

        out.print(Cli.center(stringBuilder.toString()));
        out.println();
    }

    @Deprecated
    void showStrongbox(String player) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("%s's strongbox:", player)).append("\n");
        viewModel.getContainer(viewModel.getPlayerData(player).orElseThrow().getStrongbox()).ifPresent(c ->
                stringBuilder.append("\n").append(new ResourceContainer(c).getString(this)));

        out.print(Cli.center(stringBuilder.toString()));
        out.println();
    }

    @Deprecated
    public void showContainers(String player) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("%s's containers:", player)).append("\n");
        viewModel.getPlayerShelves(player).forEach(c ->
                stringBuilder.append("\n").append(new ResourceContainer(c).getString(this)));

        out.print(Cli.center(stringBuilder.toString()));
        out.println();
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
