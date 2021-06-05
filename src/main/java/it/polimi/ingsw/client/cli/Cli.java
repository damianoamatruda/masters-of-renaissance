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
    static final int width = 160;

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

    /* public static String spaced(String s) {
        return String.format("%n%s%n", s);
    } */

    public void start() {
        setState(new SplashState());
    }

    public void stop() {
        if (network != null)
            network.stop();
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
    void setState(CliState state) {
        this.state = state;
        out.println();
        clear();
        out.println(center(String.format("\u001b[31m%s\u001B[0m", state.getClass().getSimpleName())));
        state.render(this);
    }

    void clear() {
        out.print("\033[H\033[2J");
        out.flush();
    }

    void pause() {
        in.nextLine();
    }

    void promptPause() {
        out.println("[Press ENTER to continue]");
        pause();
    }

    public void trackSlimLine() {
        for (int i = 0; i < width; i++)
            out.print("─");
        out.println();
    }

    void repeatState(String s) {
        out.println(s);
        promptPause();
        setState(this.state);
    }

    void startOfflineClient() {
        network = new OfflineClient(view, gameConfigStream);
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
            input = prompt("Shelf ID");
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
        if (viewModel.getPlayerWarehouseShelves(player).size() > 0) {
            out.printf("%s's warehouse shelves:%n", player);
            viewModel.getPlayerWarehouseShelves(player).forEach(c -> {
                out.println();
                new ResourceContainer(c).render(this);
            });
        }
        if (viewModel.getPlayerDepots(player).size() > 0) {
            out.println();
            out.printf("%s's available leader depots:%n", player);
            viewModel.getPlayerDepots(player).forEach(c -> {
                out.println();
                new ResourceContainer(c).render(this);
            });
        }
    }

    @Deprecated
    void showStrongbox(String player) {
        out.printf("%s's strongbox:%n", player);
        out.println();
        viewModel.getContainer(viewModel.getPlayerData(player).getStrongbox()).ifPresent(c -> {
            new ResourceContainer(c).render(this);
            out.println();
        });
    }

    void quit() {
        stop();
        clear();
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
