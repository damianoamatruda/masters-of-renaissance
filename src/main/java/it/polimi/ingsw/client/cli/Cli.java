package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.OfflineClient;
import it.polimi.ingsw.client.OnlineClient;
import it.polimi.ingsw.client.cli.components.Resource;
import it.polimi.ingsw.client.cli.components.ResourceContainers;
import it.polimi.ingsw.client.cli.components.ResourceMap;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.Network;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.Event;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Cli {
    private static final Logger LOGGER = Logger.getLogger(Cli.class.getName());
    private static final int width = 179;
    private static final String backValue = " ";

    private volatile boolean running;
    private volatile boolean ready;

    private final View view;
    private Network client;

    /** The current state of the interface. */
    private CliState state;

    private final ViewModel viewModel;

    private final PrintStream out;
    private final Scanner in;

    private InputStream gameConfigStream;
    private boolean offline;

    public Cli() {
        this.view = new View();
        setViewListeners();

        this.client = null;

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

    public static String center(String str, int width, char fill) {
        if (str.lines().count() == 0)
            return "";

        if (maxLineWidth(str) > width) {
            throw new IllegalArgumentException("String too long:" + "\n" +
                    str.lines().filter(s -> s.length() == maxLineWidth(str)).findAny().orElseThrow() + "\n" +
                    String.format("Max length: %d. Got: %d.", width, textLength(str.lines().filter(s -> s.length() == maxLineWidth(str)).findAny().orElseThrow())));
        }

        int marginLeft = (width - maxLineWidth(str)) / 2;

        StringBuilder stringBuilder = new StringBuilder();
        str.lines().forEachOrdered(line -> stringBuilder.append(Character.toString(fill).repeat(marginLeft)).append(line).append(Character.toString(fill).repeat(width - marginLeft - textLength(line))).append("\n"));
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

    public static String center(String str, int width) {
        return center(str, width, ' ');
    }

    public static String center(String str) {
        return center(str, width);
    }

    public static String left(String str, int width, char fill) {
        if (textLength(str) > width) {
            throw new IllegalArgumentException("String too long:" + "\n" +
                    str + "\n" +
                    String.format("Max length: %d. Got: %d.", width, textLength(str)));
        }
        return str + Character.toString(fill).repeat(width - textLength(str));
    }

    public static String left(String str, int width) {
        return left(str, width, ' ');
    }

    public static String left(String str) {
        return left(str, width);
    }

    public static String right(String str, int width, char fill) {
        return Character.toString(fill).repeat(width - textLength(str)) + str;
    }

    public static String right(String str, int width) {
        return right(str, width, ' ');
    }

    public static String right(String str) {
        return right(str, width);
    }

    public static int textLength(String str) {
        return str.replaceAll("\u001B\\[[;\\d]*m", "").length();
    }

    public static int maxLineWidth(String str) {
        return str.lines().mapToInt(Cli::textLength).max().orElseThrow();
    }

    public static int maxLinesHeight(List<String> str) {
        return Math.toIntExact(str.stream().map(String::lines).mapToLong(Stream::count).max().orElseThrow());
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
        closeClient();
        view.close();
    }

    public void dispatch(Event event) {
        view.dispatch(event);
    }

    public Optional<String> prompt(String prompt, String defaultValue) {
        if (prompt.isEmpty())
            throw new IllegalArgumentException("Prompt cannot be empty.");
        out.print(right(String.format("%s (default: %s): ", prompt, defaultValue), width / 3));
        String value = in.nextLine();
        if (value.equals(backValue))
            return Optional.empty();
        return Optional.of(!value.isBlank() ? value : defaultValue);
    }

    public Optional<String> prompt(String prompt) {
        if (!prompt.isEmpty())
            out.print(right(String.format("%s: ", prompt), width / 3));
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
        out.println();
        out.println(center("[Press ENTER to continue]"));
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

    void openOfflineClient() {
        closeClient();
        client = new OfflineClient(view, gameConfigStream);
        client.open();
        offline = true;
    }

    void openOnlineClient(String host, int port) throws IOException {
        closeClient();
        client = new OnlineClient(view, host, port);
        client.open();
        offline = false;
    }

    void closeClient() {
        if (client != null) {
            client.close();
            client = null;
        }
    }

    // TODO: Maybe make it a component, like Menu
    Optional<Map<Integer, Map<String, Integer>>> promptShelves(Map<String, Integer> resMap, Set<Integer> allowedShelves, boolean discardable) {
        Map<Integer, Map<String, Integer>> shelves = new HashMap<>();

        new ResourceContainers(
                viewModel.getLocalPlayerNickname(),
                viewModel.getPlayerWarehouseShelves(viewModel.getLocalPlayerNickname()),
                viewModel.getPlayerDepots(viewModel.getLocalPlayerNickname()),
                null)
                .render(this);

        Map<String, Integer> remainingResMap = new HashMap<>(resMap);
        int totalQuantity = remainingResMap.values().stream().mapToInt(Integer::intValue).sum();
        AtomicInteger allocQuantity = new AtomicInteger();
        while (allocQuantity.get() < totalQuantity) {
            out.println();

            String string = "Remaining resources:" + "\n" +
                    new ResourceMap(remainingResMap).getString(this);
            out.println(center(string));

            AtomicBoolean valid = new AtomicBoolean(true);
            AtomicBoolean discarded = new AtomicBoolean(false);

            if (discardable) {
                out.println();
                out.println(center("Press D if you want to discard all remaining resources."));

                out.println();
                prompt("").ifPresentOrElse(input -> {
                    if (input.equalsIgnoreCase("D"))
                        discarded.set(true);
                }, () -> valid.set(false));
            }

            if (discarded.get())
                break;

            if (valid.get()) {
                out.println();
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
            }

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

        new ResourceContainers(
                viewModel.getLocalPlayerNickname(),
                viewModel.getPlayerWarehouseShelves(viewModel.getLocalPlayerNickname()),
                viewModel.getPlayerDepots(viewModel.getLocalPlayerNickname()),
                null)
                .render(this);

        AtomicInteger allocQuantity = new AtomicInteger();
        while (allocQuantity.get() < totalQuantity) {
            out.println();
            out.printf("You can choose %d resources among these:%n", totalQuantity - allocQuantity.get());

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

    private void setViewListeners() {
        this.view.setResQuitEventListener(event -> state.on(this, event));
        this.view.setUpdateBookedSeatsEventListener(event -> state.on(this, event));
        this.view.setUpdateJoinGameEventListener(event -> state.on(this, event));
        this.view.setErrNewGameEventListener(event -> state.on(this, event));
        this.view.setErrNicknameEventListener(event -> state.on(this, event));
        this.view.setErrActionEventListener(event -> state.on(this, event));
        this.view.setErrActiveLeaderDiscardedEventListener(event -> state.on(this, event));
        this.view.setErrBuyDevCardEventListener(event -> state.on(this, event));
        this.view.setErrCardRequirementsEventListener(event -> state.on(this, event));
        this.view.setErrInitialChoiceEventListener(event -> state.on(this, event));
        this.view.setErrNoSuchEntityEventListener(event -> state.on(this, event));
        this.view.setErrObjectNotOwnedEventListener(event -> state.on(this, event));
        this.view.setErrReplacedTransRecipeEventListener(event -> state.on(this, event));
        this.view.setErrResourceReplacementEventListener(event -> state.on(this, event));
        this.view.setErrResourceTransferEventListener(event -> state.on(this, event));
        this.view.setUpdateActionEventListener(event -> state.on(this, event));
        this.view.setUpdateActionTokenEventListener(event -> state.on(this, event));
        this.view.setUpdateCurrentPlayerEventListener(event -> state.on(this, event));
        this.view.setUpdateDevCardGridEventListener(event -> state.on(this, event));
        this.view.setUpdateDevCardSlotEventListener(event -> state.on(this, event));
        this.view.setUpdateFaithPointsEventListener(event -> state.on(this, event));
        this.view.setUpdateGameEventListener(event -> state.on(this, event));
        this.view.setUpdateGameEndEventListener(event -> state.on(this, event));
        this.view.setUpdateLastRoundEventListener(event -> state.on(this, event));
        this.view.setUpdateActivateLeaderEventListener(event -> state.on(this, event));
        this.view.setUpdateLeadersHandCountEventListener(event -> state.on(this, event));
        this.view.setUpdateMarketEventListener(event -> state.on(this, event));
        this.view.setUpdatePlayerEventListener(event -> state.on(this, event));
        this.view.setUpdatePlayerStatusEventListener(event -> state.on(this, event));
        this.view.setUpdateResourceContainerEventListener(event -> state.on(this, event));
        this.view.setUpdateSetupDoneEventListener(event -> state.on(this, event));
        this.view.setUpdateVaticanSectionEventListener(event -> state.on(this, event));
        this.view.setUpdateVictoryPointsEventListener(event -> state.on(this, event));
        this.view.setUpdateLeadersHandEventListener(event -> state.on(this, event));
    }
}
