package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.UiController;
import it.polimi.ingsw.client.cli.components.*;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirementEntry;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static it.polimi.ingsw.client.cli.Cli.center;

public abstract class CliController extends UiController implements Renderable {
    protected final Cli cli = Cli.getInstance();

    public CliController() {
        super(Cli.getInstance().getUi());
    }

    /**
     * Sets the next state based on the following algorithm:
     *
     * UpdateGame tells the client whether the game's setup phase is still ongoing or not.
     * If the setup is done, the client needs to wait until UpdateCurrentPlayer to know which state to change to.
     * If the setup phase is not done:
     * The local player's UpdatePlayer tells the client what part of the player setup to switch to.
     * If the leaders hand still needs to be chosen the client will need to wait for UpdateLeadersHand.
     */
    protected void setNextState() {
        vm.isSetupDone().ifPresent(isSetupDone -> { // received UpdateGame (if not, wait for it)
            vm.getLocalPlayer().flatMap(vm::getPlayerData).ifPresent(pd -> {
                if (isSetupDone && vm.getCurrentPlayer().isPresent() &&
                        !vm.getLocalPlayer().map(vm::getPlayerLeaderCards).orElseThrow().isEmpty()) { // setup is done
                    if (vm.isCurrentPlayer())
                        cli.setController(new TurnBeforeActionController(), false);
                    else
                        cli.setController(new WaitingAfterTurnController(), false);
                } else if (!isSetupDone) // setup not done
                    pd.getSetup().ifPresent(setup -> { // received local player's setup
                        if (isLeaderSetupAvailable())
                            cli.setController(new SetupLeadersController(), false);
                        else if (isLocalLeaderSetupDone())
                            cli.setController(new SetupResourcesController(), false);
                    });
            });
        });
    }

    @Override
    public abstract void render();

    @Override
    public void on(ErrAction event) {
        super.on(event);

        switch (event.getReason()) {
            case LATE_SETUP_ACTION -> {
                cli.getOut().println(center("Setup phase is concluded. Advancing to game turns."));
                if (vm.isCurrentPlayer())
                    cli.setController(new TurnBeforeActionController(), true);
                else
                    cli.setController(new WaitingAfterTurnController(), true);
            }
            case EARLY_MANDATORY_ACTION -> {
                cli.getOut().println(center("Setup phase is not concluded yet. Returning to setup phase."));
                if (!isLocalLeaderSetupDone())
                    cli.setController(new SetupLeadersController(), true);
                else if (!isLocalResourceSetupDone())
                    cli.setController(new SetupResourcesController(), true);
                else
                    setNextState();
            }
            case LATE_MANDATORY_ACTION -> {
                cli.getOut().println(center("You have already done a mandatory action. Advancing to optional actions."));
                cli.setController(new TurnAfterActionController(), true);
            }
            case EARLY_TURN_END -> {
                cli.getOut().println(center("You cannot end the turn yet. A mandatory action needs to be done before ending the turn."));
                cli.setController(new TurnBeforeActionController(), true);
            }
            case GAME_ENDED -> {
                cli.getOut().println(center("The game has ended. Advancing to ending screen."));
                cli.setController(new GameEndController(), true);
            }
            case NOT_CURRENT_PLAYER -> {
                cli.getOut().println(center("You are not the current player. Please wait for your turn."));
                cli.setController(new WaitingAfterTurnController(), true);
            }
        }
    }

    @Override
    public void on(ErrActiveLeaderDiscarded event) {
        super.on(event);
    }

    @Override
    public void on(ErrBuyDevCard event) {
        super.on(event);
    }

    @Override
    public void on(ErrCardRequirements event) {
        cli.reloadController("You cannot activate the leader card. " +
                event.getMissingDevCards().map(missingDevCards -> {
                    StringBuilder msg = new StringBuilder("Missing development cards:\n\n");

                    for (ReducedDevCardRequirementEntry e : missingDevCards)
                        msg.append(String.format("%s %s × %d\n",
                                e.getColor(),
                                e.getLevel() == 0 ? "any level" : String.format("level %d", e.getLevel()),
                                e.getAmount()));

                    return msg.toString();
                }).orElse(event.getMissingResources().map(missingResources -> {
                    StringBuilder msg = new StringBuilder("Missing resources:\n\n");

                    for (Map.Entry<String, Integer> e : missingResources.entrySet())
                        msg.append(String.format("%s × %d\n", e.getKey(), e.getValue()));

                    return msg.toString();
                }).orElse("")));
    }

    @Override
    public void on(ErrInitialChoice event) {
        super.on(event);
    }

    @Override
    public void on(ErrNewGame event) {
        super.on(event);
    }

    @Override
    public void on(ErrNickname event) {
        super.on(event);

        switch (event.getReason()) {
            case ALREADY_SET -> cli.reloadController("Error setting nickname: nickname is already set.");
            case TAKEN -> cli.reloadController("Error setting nickname: nickname is taken.");
            case NOT_SET -> cli.reloadController("Error setting nickname: nickname is blank.");
            case NOT_IN_GAME -> {
                cli.getOut().println("Match not joined yet.");

                cli.setController(new InputNicknameController(cli.getUi().isOffline() ? "Play Offline" : "Play Online"), true);
            }
        }
    }

    @Override
    public void on(ErrNoSuchEntity event) {
        super.on(event);

        cli.reloadController(
                String.format("No such entity %s: %s%s.",
                        event.getOriginalEntity().toString().toLowerCase().replace("_", " "),
                        event.getId() < 0 ? "" : String.format("ID %d", event.getId()),
                        event.getCode() == null ? "" : String.format("code %s", event.getCode())));
    }

    @Override
    public void on(ErrObjectNotOwned event) {
        super.on(event);

        cli.reloadController(String.format("%s with ID %d isn't yours. Are you sure you typed that right?", event.getObjectType(), event.getId()));
    }

    @Override
    public void on(ErrReplacedTransRecipe event) {
        super.on(event);

        if (event.isIllegalDiscardedOut())
            cli.reloadController("Output of resource transfer cannot be discarded");
        else
            cli.reloadController(String.format(
                    "Irregular amount of %s specified in the container map: %d requested, %d chosen.",
                    event.getResType().isEmpty() ? "resources" : event.getResType(),
                    event.getReplacedCount(),
                    event.getShelvesChoiceResCount()));
    }

    @Override
    public void on(ErrResourceReplacement event) {
        super.on(event);

        cli.reloadController(String.format("Invalid transaction %s: %s resource given.",
                event.isInput() ? "input" : "output",
                event.isNonStorable() ? "non-storable" : "excluded"));
    }

    @Override
    public void on(ErrResourceTransfer event) {
        super.on(event);

        String reason = switch (event.getReason()) {
            case BOUNDED_RESTYPE_DIFFER -> event.getResType() != null ?
                    "shelf's binding resource type is different from transferring resource" :
                    "multiple resource types cannot be bound to the same shelf";
            case NON_STORABLE -> "resource is not storable";
            case CAPACITY_REACHED -> "shelf's capacity boundaries reached";
            case DUPLICATE_BOUNDED_RESOURCE -> "resource type is already bound to another shelf";
        };

        cli.reloadController(String.format(event.isAdded() ? "You cannot add%s into container: %s." : "You cannot remove%s from container:",
                event.getResType() == null ? "" : String.format(" %s", event.getResType()), // TODO: Create error for this
                reason));
    }

    @Override
    public void on(ErrServerUnavailable event) {
        cli.getOut().println("Server disconnected, returning to main menu");

        cli.setController(new MainMenuController(), true);
    }

    @Override
    public void on(ResQuit event) {
        super.on(event);

        cli.setController(new MainMenuController(), false);
    }

    @Override
    public void on(UpdateActionToken event) {
        super.on(event);

        // print only, no cache update
        vm.getActionToken(event.getActionToken())
                .ifPresent(t -> {
                    cli.getOut().println();
                    new ActionToken(t).render();
                });
    }

    @Override
    public void on(UpdateBookedSeats event) {
        super.on(event);
    }

    @Override
    public void on(UpdateCurrentPlayer event) {
        super.on(event);
        
        // DO NOT DO STATE SWITCHING HERE
        cli.getOut().println(Cli.center(String.format("Current player: %s", event.getPlayer())));
    }

    @Override
    public void on(UpdateDevCardGrid event) {
        super.on(event);

        vm.getDevCardGrid().ifPresent(devCardGrid -> {
            cli.getOut().println();
            new DevCardGrid(devCardGrid).render();
        });
    }

    @Override
    public void on(UpdateDevSlot event) {
        super.on(event);

        new DevSlots(vm.getPlayerDevelopmentSlots(event.getPlayer())).render();
    }

    @Override
    public void on(UpdateFaithPoints event) {
        super.on(event);

        Map<String, Integer> points = vm.getPlayerNicknames().stream()
                .collect(Collectors.toMap(n -> n, vm::getPlayerFaithPoints));

        vm.getFaithTrack().ifPresent(faithTrack -> {
            cli.getOut().println();
            new FaithTrack(faithTrack, points).render();
        });
    }

    @Override
    public void on(UpdateGame event) {
        super.on(event);

        Map<String, Integer> points = vm.getPlayerNicknames().stream()
                .collect(Collectors.toMap(nick -> nick, nick -> 0));

        vm.getFaithTrack().ifPresent(faithTrack -> {
            cli.getOut().println();
            new FaithTrack(faithTrack, points).render();
        });
    }

    @Override
    public void on(UpdateGameEnd event) {
        super.on(event);

        cli.getOut().println();
        cli.getOut().println("Game ended!");
    }

    @Override
    public void on(UpdateLastRound event) {
        super.on(event);

        cli.getOut().println();
        cli.getOut().println("Last round!");
    }

    @Override
    public void on(UpdateActivateLeader event) {
        super.on(event);

        cli.getOut().println();
        cli.getOut().println(Cli.center(String.format("%s activated leader card %d.\n", vm.getCurrentPlayer().get(), event.getLeader())));
        cli.getOut().println();
        cli.getOut().println(Cli.center(new Box(new LeaderCard(vm.getLeaderCard(event.getLeader()).orElseThrow())).getString()));
    }

    @Override
    public void on(UpdateLeadersHand event) {
        super.on(event);

        cli.getOut().println();
        cli.getOut().println(center(String.format("%s's leader cards:", event.getPlayer())));
        cli.getOut().println();
        new LeadersHand(vm.getPlayerLeaderCards(event.getPlayer())).render();
    }

    @Override
    public void on(UpdateLeadersHandCount event) {
        super.on(event);

        cli.getOut().println();
        cli.getOut().println(center(String.format("Player %s now has %d leader cards.", event.getPlayer(), event.getLeadersCount())));
        new Thread(cli::promptPause).start();
    }

    @Override
    public void on(UpdateMarket event) {
        super.on(event);

        cli.getOut().println();
        new Market(event.getMarket()).render();
    }

    @Override
    public void on(UpdatePlayer event) {
        super.on(event);

        cli.getOut().println();
        new ResourceContainers(
                event.getPlayer(),
                vm.getPlayerWarehouseShelves(event.getPlayer()),
                vm.getPlayerDepots(event.getPlayer()),
                vm.getPlayerStrongbox(event.getPlayer()).orElse(null))
                .render();

        Optional<ReducedResourceTransactionRecipe> baseProds;

        baseProds = vm.getLocalPlayer().map(vm::getPlayerBaseProduction).orElseThrow();
        // TODO put baseProd outside of PlayerData, since it is unique (?)

        cli.getOut().println(center("\nBase Production:"));
        cli.getOut().println(center(new Box(new ResourceTransactionRecipe(baseProds.orElseThrow()), -1).getString()));
    }

    @Override
    public void on(UpdatePlayerStatus event) {
        super.on(event);

        cli.getOut().println();
        cli.getOut().printf("Player %s %s.%n", event.getPlayer(), event.isActive() ? "reconnected" : "disconnected");
    }

    @Override
    public void on(UpdateResourceContainer event) {
        super.on(event);

        cli.getOut().println();
        cli.getOut().println(center(new Box(new ResourceContainer(event.getResContainer())).getString()));
    }

    @Override
    public void on(UpdateSetupDone event) {
        super.on(event);

        if (vm.getPlayerNicknames().size() > 1) {
            cli.getOut().println();
            cli.getOut().println("All players have finished their setup! Game starting...");
        }
    }

    @Override
    public void on(UpdateVaticanSection event) {
        super.on(event);

        cli.getOut().println();
        cli.getOut().printf("Activated vatican section %d%n", event.getVaticanSection()); // TODO: improve
    }

    @Override
    public void on(UpdateVictoryPoints event) {
        super.on(event);

        cli.getOut().println();
        cli.getOut().println(Cli.center(
                String.format("Victory points for %s: %d.\n", event.getPlayer(), event.getVictoryPoints())));
    }
}
