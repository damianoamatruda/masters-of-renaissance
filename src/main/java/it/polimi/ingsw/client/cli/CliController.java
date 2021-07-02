package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.UiController;
import it.polimi.ingsw.client.cli.components.*;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirementEntry;
import it.polimi.ingsw.common.reducedmodel.ReducedPlayer;

import java.util.Map;
import java.util.stream.Collectors;

import static it.polimi.ingsw.client.cli.Cli.center;
import static it.polimi.ingsw.client.cli.Cli.getInstance;

public abstract class CliController extends UiController implements Renderable {
    protected final Cli cli = getInstance();

    public CliController() {
        super(getInstance().getUi());
    }

    /**
     * Sets the next state based on the following algorithm:
     * <p>
     * Differentiation between setup phase and turn phase
     *  -> check isSetupDone to know which phase to limit the choice to
     * If the setup phase is not concluded:
     *  -> check whether the leader setup still needs to be done,
     *     else go to the resource setup, which will internally choose
     *         whether it still needs to be done or whether it has to simply show the waiting screen
     * If the setup phase is concluded:
     *  -> check whether the current player and the local player
     *     are the same player and switch accordingly
     */
    protected void setNextState() {
        if (vm.isGameEnded())
            cli.setController(new EndGameController(), true);
        else
            vm.isSetupDone().ifPresent(isSetupDone -> {
                vm.getLocalPlayer().flatMap(vm::getPlayer).ifPresent(player -> {
                    if (isSetupDone && vm.getCurrentPlayer().isPresent() &&
                            !vm.getPlayerLeaderCards(vm.getLocalPlayer().get()).isEmpty()) { // setup is done
                        if (vm.localPlayerIsCurrent())
                            cli.setController(new TurnBeforeActionController(), true);
                        else
                            cli.setController(new WaitingAfterTurnController(), true);
                    } else if (!isSetupDone) // setup not done
                        if (isLeaderSetupAvailable())
                            cli.setController(new SetupLeadersController(), true);
                        else if (isLocalLeaderSetupDone())
                            cli.setController(new SetupResourcesController(), true);
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
                cli.getOut().println(center("\nSetup phase is concluded. Advancing to game turns."));
                if (vm.localPlayerIsCurrent())
                    cli.setController(new TurnBeforeActionController(), true);
                else
                    cli.setController(new WaitingAfterTurnController(), true);
            }
            case EARLY_MANDATORY_ACTION -> {
                cli.getOut().println(center("\nSetup phase is not concluded yet. Returning to setup phase."));
                if (!isLocalLeaderSetupDone())
                    cli.setController(new SetupLeadersController(), true);
                else if (!isLocalResourceSetupDone())
                    cli.setController(new SetupResourcesController(), true);
                else
                    setNextState();
            }
            case LATE_MANDATORY_ACTION -> {
                cli.getOut().println(center("\nYou have already done a mandatory action. Advancing to optional actions."));
                cli.setController(new TurnAfterActionController(), true);
            }
            case EARLY_TURN_END -> {
                cli.getOut().println(center("\nYou cannot end the turn yet. A mandatory action needs to be done before ending the turn."));
                cli.setController(new TurnBeforeActionController(), true);
            }
            case GAME_ENDED -> {
                cli.getOut().println(center("\nThe game has ended. Advancing to ending screen."));
                cli.setController(new EndGameController(), true);
            }
            case NOT_CURRENT_PLAYER -> {
                cli.getOut().println(center("\nYou are not the current player. Please wait for your turn."));
                cli.setController(new WaitingAfterTurnController(), true);
            }
        }
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
    public void on(ErrNickname event) {
        super.on(event);

        switch (event.getReason()) {
            case ALREADY_SET -> cli.reloadController("Error setting nickname: nickname is already set.");
            case TAKEN -> cli.reloadController("Error setting nickname: nickname is taken.");
            case NOT_SET -> cli.reloadController("Error setting nickname: nickname is blank.");
            case NOT_IN_GAME -> {
                cli.getOut().println("\nMatch not joined yet.");
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
            cli.reloadController("The payment's cost is erroneously specified, please choose all and only the needed resources.");
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
        super.on(event);

        if (!cli.getUi().hasClientOpen())
            return;

        cli.getUi().closeClient();

        cli.getOut().println(center("\nServer is down. Try again later."));
        cli.setReturningToMainMenu();
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
    public void on(UpdateCurrentPlayer event) {
        super.on(event);

        // DO NOT DO STATE SWITCHING HERE
        cli.getOut().println();
        cli.getOut().println(center(String.format("Current player: %s", event.getPlayer())));
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

        cli.getOut().println();
        new DevSlots(vm.getPlayerDevelopmentSlots(event.getPlayer())).render();
    }

    @Override
    public void on(UpdateFaithPoints event) {
        super.on(event);

        Map<String, Integer> points = vm.getPlayers().stream()
                .map(ReducedPlayer::getNickname)
                .collect(Collectors.toMap(n -> n, vm::getPlayerFaithPoints));

        vm.getFaithTrack().ifPresent(faithTrack -> {
            cli.getOut().println();
            new FaithTrack(faithTrack, points).render();
        });
    }

    @Override
    public void on(UpdateGame event) {
        super.on(event);

        Map<String, Integer> points = vm.getPlayers().stream()
                .map(ReducedPlayer::getNickname)
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
        cli.getOut().println(center(String.format("%s activated leader card %d.\n", vm.getCurrentPlayer().get(), event.getLeader())));
        cli.getOut().println();
        cli.getOut().println(center(new Box(new LeaderCard(vm.getLeaderCard(event.getLeader()).orElseThrow())).getString()));
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

        cli.getOut().println(center(String.format("\nPlayer %s now has %d leader cards.", event.getPlayer(), event.getLeadersHandCount())));
    }

    @Override
    public void on(UpdateMarket event) {
        super.on(event);

        cli.getOut().println();
        new Market(event.getMarket()).render();
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

        if (vm.getPlayers().size() > 1) {
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
        cli.getOut().println(center(
                String.format("Victory points for %s: %d.\n", event.getPlayer(), event.getVictoryPoints())));
    }
}
