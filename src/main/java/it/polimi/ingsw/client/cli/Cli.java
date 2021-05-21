package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.LocalClient;
import it.polimi.ingsw.client.NetworkClient;
import it.polimi.ingsw.client.ReducedObjectPrinter;
import it.polimi.ingsw.client.Ui;
import it.polimi.ingsw.common.EventDispatcher;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.common.events.vcevents.*;
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

public class Cli extends EventDispatcher implements Ui {
    static final int width = 80;

    private final View view;

    /** The current state of the interface. */
    private CliState state;

    private final ReducedGame cache;
    private final ReducedObjectPrinter printer;

    private final Scanner scanner;

    private final BlockingQueue<CliState> stateQueue;

    private final VCEvent lastReq;

    private boolean singleplayer;

    public Cli() {
        this.view = new CliView();
        this.stateQueue = new LinkedBlockingDeque<>();
        this.stateQueue.add(new SplashState());

        this.cache = new ReducedGame();
        this.printer = new CliReducedObjectPrinter(cache);
        this.cache.setPrinter(printer);
        this.scanner = new Scanner(System.in);

        this.lastReq = null;
        this.singleplayer = false;
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

    String prompt(PrintStream out, Scanner in, String prompt, String defaultValue) {
        out.printf("%s (default: %s): ", prompt, defaultValue);
        String value = in.nextLine();
        return !value.isBlank() ? value : defaultValue;
    }

    String prompt(PrintStream out, Scanner in, String prompt) {
        if (!prompt.isEmpty())
            out.printf("%s: ", prompt);
        String input = in.nextLine();
        if (input.toUpperCase().startsWith("Q"))
            dispatch(new ReqQuit());
        return input;
    }

    public CliState getState() {
        return state;
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
            this.state = stateQueue.take();
        } catch (InterruptedException e) { e.printStackTrace(); }
        
        while (this.state != null) {
            System.out.println(state.getClass().getSimpleName());
            state.render(this, System.out, scanner, cache, printer);
            try {
                this.state = stateQueue.take();
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }
    
    public void registerOnMV(EventDispatcher view) {
        view.addEventListener(ErrAction.class, this::on);
        view.addEventListener(ErrActiveLeaderDiscarded.class, this::on);
        view.addEventListener(ErrBuyDevCard.class, this::on);
        view.addEventListener(ErrCardRequirements.class, this::on);
        view.addEventListener(ErrInitialChoice.class, this::on);
        view.addEventListener(ErrNewGame.class, this::on);
        view.addEventListener(ErrNickname.class, this::on);
        view.addEventListener(ErrObjectNotOwned.class, this::on);
        view.addEventListener(ErrReplacedTransRecipe.class, this::on);
        view.addEventListener(ErrReplacedTransRecipe.class, this::on);
        view.addEventListener(ErrResourceReplacement.class, this::on);
        view.addEventListener(ErrResourceTransfer.class, this::on);
        view.addEventListener(UpdateBookedSeats.class, this::on);
        view.addEventListener(UpdateJoinGame.class, this::on);
        view.addEventListener(UpdateGameStart.class, this::on);
        view.addEventListener(UpdateCurrentPlayer.class, this::on);
        view.addEventListener(UpdateSetupDone.class, this::on);
        view.addEventListener(UpdateGameResume.class, this::on);
        view.addEventListener(UpdateLastRound.class, this::on);
        view.addEventListener(UpdateGameEnd.class, this::on);
        view.addEventListener(UpdatePlayer.class, this::on);
        view.addEventListener(UpdateLeadersHandCount.class, this::on);
        view.addEventListener(UpdateFaithPoints.class, this::on);
        view.addEventListener(UpdateVictoryPoints.class, this::on);
        view.addEventListener(UpdatePlayerStatus.class, this::on);
        view.addEventListener(UpdateDevCardSlot.class, this::on);
        view.addEventListener(UpdateLeader.class, this::on);
        view.addEventListener(UpdateResourceContainer.class, this::on);
        view.addEventListener(UpdateDevCardGrid.class, this::on);
        view.addEventListener(UpdateMarket.class, this::on);
        view.addEventListener(UpdateVaticanSection.class, this::on);
        view.addEventListener(UpdateActionToken.class, this::on);
        view.addEventListener(UpdateLeadersHand.class, this::on);
    }

    public void unregisterOnMV(EventDispatcher view) {
        view.removeEventListener(ErrAction.class, this::on);
        view.removeEventListener(ErrActiveLeaderDiscarded.class, this::on);
        view.removeEventListener(ErrBuyDevCard.class, this::on);
        view.removeEventListener(ErrCardRequirements.class, this::on);
        view.removeEventListener(ErrInitialChoice.class, this::on);
        view.removeEventListener(ErrNewGame.class, this::on);
        view.removeEventListener(ErrNickname.class, this::on);
        view.removeEventListener(ErrObjectNotOwned.class, this::on);
        view.removeEventListener(ErrReplacedTransRecipe.class, this::on);
        view.removeEventListener(ErrReplacedTransRecipe.class, this::on);
        view.removeEventListener(ErrResourceReplacement.class, this::on);
        view.removeEventListener(ErrResourceTransfer.class, this::on);
        view.removeEventListener(UpdateBookedSeats.class, this::on);
        view.removeEventListener(UpdateJoinGame.class, this::on);
        view.removeEventListener(UpdateGameStart.class, this::on);
        view.removeEventListener(UpdateCurrentPlayer.class, this::on);
        view.removeEventListener(UpdateSetupDone.class, this::on);
        view.removeEventListener(UpdateGameResume.class, this::on);
        view.removeEventListener(UpdateLastRound.class, this::on);
        view.removeEventListener(UpdateGameEnd.class, this::on);
        view.removeEventListener(UpdatePlayer.class, this::on);
        view.removeEventListener(UpdateLeadersHandCount.class, this::on);
        view.removeEventListener(UpdateFaithPoints.class, this::on);
        view.removeEventListener(UpdateVictoryPoints.class, this::on);
        view.removeEventListener(UpdatePlayerStatus.class, this::on);
        view.removeEventListener(UpdateDevCardSlot.class, this::on);
        view.removeEventListener(UpdateLeader.class, this::on);
        view.removeEventListener(UpdateResourceContainer.class, this::on);
        view.removeEventListener(UpdateDevCardGrid.class, this::on);
        view.removeEventListener(UpdateMarket.class, this::on);
        view.removeEventListener(UpdateVaticanSection.class, this::on);
        view.removeEventListener(UpdateActionToken.class, this::on);
        view.removeEventListener(UpdateLeadersHand.class, this::on);
    }

    void startNetworkClient(String host, int port) {
        boolean connected = true;
        try {
            new NetworkClient(host, port, view, this).start();
        } catch (IOException e) {
            connected = false;
            // TODO: setState (error)
        }
        if (connected) {
            singleplayer = false;
            setState(new InputNicknameState());
        }
    }

    void startLocalClient() {
        new LocalClient(view, this).start();
        singleplayer = true;
        setState(new InputNicknameState());
    }

    public Map<Integer, Map<String, Integer>> promptShelves(PrintStream out, Scanner in) {
        System.out.println("Choose mapping shelf-resource-quantity:");
        final Map<Integer, Map<String, Integer>> shelves = new HashMap<>();
        int container;
        String resource;
        int amount;

        String input = "";
        while (!input.equalsIgnoreCase("Y")) {
            input = prompt(out, in, "Which container? (Input an ID, or else Enter to skip)");
            if(input.isEmpty())
                break;

            try {
                container = Integer.parseInt(input);
//                int finalContainer = container;
//                if(cache.getContainers().stream().filter(c -> c.getId() == finalContainer).findAny().isEmpty()) {
//                    System.out.println("You do not own this container. Try again");
//                    continue;
//                };
            } catch (NumberFormatException e) {
                System.out.println("Please input an integer.");
                continue;
            }

            resource = prompt(out, in, "Which resource?");

            input = prompt(out, in, "How many?");
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
            input = prompt(out, in, "Are you done choosing? [Y/*]");
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
        System.out.println("Choosing blank resource replacements:");

        String resource;
        int amount;
        String input;
        Map<String, Integer> result = new HashMap<>();

        input = "";
        while (!input.equalsIgnoreCase("Y")) {
            resource = prompt(out, in, "Which resource do you want as replacement to Zeros/Blanks? (Enter to skip)");
            if(resource.isEmpty())
                break;

            input = prompt(out, in, "How many blanks would you like to replace?");
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

            input = prompt(out, in, "Are you done choosing? [Y/*]");
        }
        return result;
    }

    public void quit() {
        PrintStream out = System.out;

        Cli.clear(out);
        renderMainTitle(out);
        for (int i = 0; i < 2; i++)
            out.println();
        out.println("Quitting in 2 seconds...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        Cli.clear(out);
        renderMainTitle(out);
        for (int i = 0; i < 2; i++)
            out.println();
        out.println("Quitting in 1 seconds...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        Cli.clear(out);
    }

    private void on(ErrAction event) {
        repeatState(event.getReason().toString());
    }

    private void on(ErrActiveLeaderDiscarded event) {
        int id = -1;
        try {
            id = ((ReqLeaderAction)lastReq).getLeader();
        } catch (Exception ignored) {}

        repeatState(String.format("Active leader %d tried to be discarded.", id));
    }

    private void on(ErrBuyDevCard event) {
        repeatState(event.isStackEmpty() ?
                "Cannot buy development card. Deck is empty." :
                "Cannot place devcard in slot, level mismatch.");
    }

    private void on(ErrCardRequirements event) {
        repeatState(event.getReason());
    }

    private void on(ErrInexistentEntity event) {
        repeatState(event.getReason());
    }

    private void on(ErrInitialChoice event) {
        repeatState(event.isLeadersChoice() ? // if the error is from the initial leaders choice
                event.getMissingLeadersCount() == 0 ?
                        "Leaders already chosen" :        // if the count is zero it means the leaders were already chosen
                        String.format("Not enough leaders chosen: %d missing.", event.getMissingLeadersCount()) :
                "Resources already chosen");          // else it's from the resources choice
    }

    private void on(ErrNewGame event) {
        repeatState(event.isInvalidPlayersCount() ?
                "Invalid players count." :
                "You are not supposed to choose the players count for the game.");
    }

    private void on(ErrNickname event) {
        repeatState("Nickname is invalid. Reason: " + event.getReason().toString().toLowerCase());
    }

    private void on(ErrObjectNotOwned event) {
        repeatState(String.format("%s %d isn't yours. Are you sure you typed that right?", event.getObjectType(), event.getId()));
    }

    private void on(ErrReplacedTransRecipe event) {
        repeatState(
                String.format(
                        "Discrepancy in the transaction recipe.\nResource: %s, replaced count: %d, specified shelfmap count: %d",
                        event.getResType(), event.getReplacedCount(), event.getShelvesChoiceResCount()));
    }

    private void on(ErrResourceReplacement event) {
        if (event.isExcluded() || event.isNonStorable())
            repeatState(String.format("Error validating transaction request %s: %s resource found.",
                    event.isInput() ? "input" : "output",
                    event.isNonStorable() ? "nonstorable" : "excluded"));
        else
            repeatState(String.format("Error validating transaction request: %d replaced resources when %d replaceable.",
                    event.getReplacedCount(),
                    event.getBlanks()));
    }

    private void on(ErrResourceTransfer event) {
        repeatState(String.format("Error transferring resources: resource %s, %s, %s.",
                event.getResType(),
                event.isAdded() ? "added" : "removed",
                event.getReason().toString()));
    }

    private void on(ErrProtocol event) {
        repeatState("Error. Elaborated input was not sent in a proper JSON format");
    }

    private void on(ErrRuntime event) {
        repeatState("Uncaught exception has been thrown. Please retry.");
    }

    private void on(ReqHeartbeat event) {
        // handled in the ClientServerHandler
        // this.on(new ResHeartbeat());
    }

    private void on(ResQuit event) {
        quit();
    }

    private void on(UpdateActionToken event) {
        printer.update(cache.getActionToken(event.getActionToken()));
    }

    private void on(UpdateBookedSeats event) {
        if (event.canPrepareNewGame().equals(cache.getNickname()) && !(getState() instanceof InputPlayersCountState)) {
            if (singleplayer)
                dispatch(new ReqNewGame(1));
            else
                setState(new InputPlayersCountState());
        } else setState(new WaitingBeforeGameState(event.getBookedSeats()));
    }

    private void on(UpdateCurrentPlayer event) {
        cache.setCurrentPlayer(event.getPlayer());
        if (!event.getPlayer().equals(cache.getNickname()))
            setState(new WaitingAfterTurnState());
        else if (!(getState() instanceof WaitingBeforeGameState) && !(getState() instanceof TurnBeforeActionState))
            setState(new TurnBeforeActionState());  // make sure the update never arrives during the own turn
    }

    private void on(UpdateDevCardGrid event) {
        cache.setDevCardGrid(event.getCards());
    }

    private void on(UpdateDevCardSlot event) {
        cache.setPlayerDevSlot(cache.getCurrentPlayer(), event.getDevSlot(), event.getDevCard());
        if (lastReq instanceof ReqBuyDevCard)
            setState(new TurnAfterActionState());
    }

    private void on(UpdateFaithPoints event) {
        cache.setFaithPoints(event.getFaithPoints());
    }

    private void on(UpdateGameEnd event) {
        cache.setWinner(event.getWinner());
        setState(new GameEndState());
    }

    private void on(UpdateGameResume event) {
        cache.setActionTokens(event.getActionTokens());
        cache.setContainers(event.getResContainers());
        cache.setDevelopmentCards(event.getDevelopmentCards());
        cache.setFaithPoints(0);
        cache.setLeaderCards(event.getLeaderCards());
        cache.setPlayers(event.getPlayers());
        cache.setProductions(event.getProductions());
        setState(new WaitingAfterTurnState());
    }

    private void on(UpdateGameStart event) {
        cache.setActionTokens(event.getActionTokens());
        cache.setContainers(event.getResContainers());
        cache.setDevelopmentCards(event.getDevelopmentCards());
        cache.setFaithPoints(0);
        cache.setLeaderCards(event.getLeaderCards());
        cache.setPlayers(event.getPlayers());
        cache.setProductions(event.getProductions());
        event.getPlayers().forEach(p -> cache.setVictoryPoints(p, 0));
    }

    private void on(UpdateJoinGame event) {
        setState(new WaitingBeforeGameState(event.getPlayersCount()));
    }

    private void on(UpdateLastRound event) {
        cache.setLastRound();
    }

    private void on(UpdateLeader event) {
        if (event.isActive())
            cache.setPlayerLeaders(cache.getCurrentPlayer(), event.getLeader());
    }

    private void on(UpdateLeadersHand event) {
        /* this message arrives last among the starting events:
            joingame
            updategamestart
            currplayer
            market
            devcardgrid
            player
            leadershand -> with GS and player has enough info for leader choice */

        event.getLeaders().forEach(id -> cache.setPlayerLeaders(event.getPlayer(), id));

        if(cache.getSetup(cache.getNickname()) != null &&
                event.getLeaders().size() > cache.getSetup(cache.getNickname()).getChosenLeadersCount()) {
            setState(new SetupLeadersState(
                    event.getLeaders().size() - cache.getSetup(cache.getNickname()).getChosenLeadersCount()));
        }
        else if(cache.getSetup(cache.getNickname()) != null &&
                cache.getSetup(cache.getNickname()).getInitialResources() > 0 &&
                !(getState() instanceof SetupResourcesState)) {
            setState(new SetupResourcesState(
                    cache.getSetup(cache.getNickname()).getInitialResources()));
        }
        else {
            if (!(getState() instanceof TurnBeforeActionState))
                setState(new TurnBeforeActionState());
        }
    }

    private void on(UpdateLeadersHandCount event) {
        cache.setPlayerLeadersCount(event.getPlayer(), event.getLeadersCount());
    }

    private void on(UpdateMarket event) {
        cache.setMarket(event.getMarket());

        /* if market update originates from my command and not from others' (base action) */
        //might be problematic if this command is sent while not current player, but immediately after sb else activates market
        if(lastReq instanceof ReqTakeFromMarket)
            setState(new TurnAfterActionState());
    }

    private void on(UpdatePlayer event) {
        cache.setBaseProduction(event.getPlayer(), event.getBaseProduction());
        cache.setPlayerWarehouseShelves(event.getPlayer(), event.getWarehouseShelves());
        cache.setPlayerStrongbox(event.getPlayer(), event.getStrongbox());

        cache.setSetup(event.getPlayer(), event.getPlayerSetup());
    }

    private void on(UpdatePlayerStatus event) {
        cache.setPlayerState(event.getPlayer(), event.isActive());
    }

    private void on(UpdateResourceContainer event) {
        cache.setContainer(event.getResContainer());

        /* if update comes from my production activation (base action) */
        if(lastReq instanceof ReqActivateProduction)
            setState(new TurnAfterActionState());
    }

    private void on(UpdateSetupDone event) {
        if (!(getState() instanceof TurnBeforeActionState))
            setState(new TurnBeforeActionState());
    }

    private void on(UpdateVaticanSection event) {
        cache.setActiveVaticanSection(event.getVaticanSection());
    }

    private void on(UpdateVictoryPoints event) {
        cache.setVictoryPoints(event.getPlayer(), event.getVictoryPoints());
    }
}
