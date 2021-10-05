package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.UiController;
import it.polimi.ingsw.client.cli.components.*;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardRequirementEntry;
import it.polimi.ingsw.common.reducedmodel.ReducedPlayer;

import java.util.Map;

import static it.polimi.ingsw.client.cli.Cli.*;

public abstract class CliController extends UiController implements Renderable {
    protected final Cli cli = getInstance();

    public CliController() {
        super(getInstance().getUi());
    }

    /**
     * Sets the next state based on the following algorithm:
     * <p>
     * Differentiation between setup phase and turn phase -> check isSetupDone to know which phase to limit the choice
     * to If the setup phase is not concluded: -> check whether the leader setup still needs to be done, else go to the
     * resource setup, which will internally choose whether it still needs to be done or whether it has to simply show
     * the waiting screen If the setup phase is concluded: -> check whether the current player and the local player are
     * the same player and switch accordingly
     */
    protected void setNextState() {
        if (vm.isGameEnded()) {
            cli.promptPause();
            cli.setController(new EndgameController());
        } else
            vm.getLocalPlayer().flatMap(vm::getPlayer).ifPresent(player -> {
                if (vm.isSetupDone() && vm.getCurrentPlayer().isPresent() &&
                        !vm.getPlayerLeaderCards(vm.getLocalPlayer().get()).isEmpty()) { // Setup is done
                    if (vm.localPlayerIsCurrent()) {
                        cli.promptPause();
                        cli.setController(new TurnBeforeActionController());
                    } else {
                        cli.promptPause();
                        cli.setController(new WaitingAfterTurnController());
                    }
                } else if (!vm.isSetupDone()) // Setup is not done
                    if (isLeaderSetupAvailable()) {
                        cli.promptPause();
                        cli.setController(new SetupLeadersController());
                    } else if (isLocalLeaderSetupDone()) {
                        cli.promptPause();
                        cli.setController(new SetupResourcesController());
                    }
            });
    }

    @Override
    public abstract void render();

    @Override
    public void on(ErrAction event) {
        super.on(event);

        switch (event.getReason()) {
            case LATE_SETUP_ACTION -> {
                cli.getOut().println();
                cli.getOut().println(center("Setup phase is concluded. Advancing to game turns."));
                setNextState();
            }
            case EARLY_MANDATORY_ACTION -> {
                cli.getOut().println();
                cli.getOut().println(center("Setup phase is not concluded yet. Returning to setup phase."));
                setNextState();
            }
            case LATE_MANDATORY_ACTION -> {
                cli.alert("You have already done a mandatory action. Advancing to optional actions.");
                cli.setController(new TurnAfterActionController());
            }
            case EARLY_TURN_END -> {
                cli.alert("You cannot end the turn yet. A mandatory action needs to be done before ending the turn.");
                cli.setController(new TurnBeforeActionController());
            }
            case GAME_ENDED -> {
                cli.alert("The game has ended. Advancing to ending screen.");
                cli.setController(new EndgameController());
            }
            case NOT_CURRENT_PLAYER -> {
                cli.alert("You are not the current player. Please wait for your turn.");
                cli.setController(new WaitingAfterTurnController());
            }
        }
    }

    @Override
    public void on(ErrCardRequirements event) {
        cli.reloadController("You cannot activate the leader card. " +
                event.getMissingDevCards().map(missingDevCards -> {
                    StringBuilder msg = new StringBuilder("Missing development cards:").append("\n").append("\n");

                    for (ReducedDevCardRequirementEntry e : missingDevCards)
                        msg.append(String.format("%s %s × %d%n",
                                e.getColor(),
                                e.getLevel() == 0 ? "any level" : String.format("level %d", e.getLevel()),
                                e.getQuantity()));

                    return msg.toString();
                }).orElse(event.getMissingResources().map(missingResources -> {
                    StringBuilder msg = new StringBuilder("Missing resources:").append("\n").append("\n");

                    for (Map.Entry<String, Integer> e : missingResources.entrySet())
                        msg.append(String.format("%s × %d%n", e.getKey(), e.getValue()));

                    return msg.toString();
                }).orElse("")));
    }

    @Override
    public void on(ErrNickname event) {
        super.on(event);

        switch (event.getReason()) {
            case ALREADY_SET -> cli.reloadController("Invalid nickname: given nickname is already set.");
            case TAKEN -> cli.reloadController("Invalid nickname: given nickname is taken.");
            case NOT_SET -> cli.reloadController("Invalid nickname: given nickname is blank.");
            case NOT_IN_GAME -> {
                cli.getOut().println();
                cli.getOut().println(center("Game not joined yet."));
                cli.promptPause();
                cli.setController(new InputNicknameController(cli.getUi().isOffline() ? "Play Offline" : "Play Online"));
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

        cli.reloadController(String.format("%s with ID %d is not yours. Are you sure you typed that right?", event.getObjectType(), event.getId()));
    }

    @Override
    public void on(ErrReplacedTransRecipe event) {
        super.on(event);

        if (event.isIllegalDiscardedOut())
            cli.reloadController("The payment's cost is erroneously specified, please choose all and only the needed resources.");
        else
            cli.reloadController(String.format(
                    "Irregular quantity of %s %s specified in the container map: %d required, %d chosen.",
                    event.isInput() ? "input" : "output",
                    event.getResType().isEmpty() ? "resources" : event.getResType(),
                    event.getReplacedCount(),
                    event.getShelvesChoiceResCount()));
    }

    @Override
    public void on(ErrInvalidResourceTransaction event) {
        super.on(event);

        String direction = event.isInput() ? "input" : "output";
        String isReplacement = event.isReplacement() ? " replacements" : "";

        switch (event.getReason()) {
            case EXCLUDED -> cli.reloadController(String.format("Invalid %s%s: excluded resource specified.",
                    direction, isReplacement));
            case ILLEGAL_NON_STORABLE -> cli.reloadController(String.format("Invalid %s%s: non-storable resource specified in container map.",
                    direction, isReplacement));
            case ILLEGAL_STORABLE -> cli.reloadController(String.format("Invalid %s%s: storable resource specified as non-storable.",
                    direction, isReplacement));
            case NEGATIVE_VALUES -> cli.reloadController(String.format("Invalid %s%s: negative quantity specified.",
                    direction, isReplacement));
        }
    }

    @Override
    public void on(ErrResourceTransfer event) {
        super.on(event);

        String reason = switch (event.getReason()) {
            case BOUNDED_RESTYPE_DIFFER -> event.getResType() != null ?
                    "shelf's binding resource type is different from transferring resource" :
                    "multiple resource types cannot be bound to the same shelf";
            case NON_STORABLE -> "resource is not storable";
            case CAPACITY_REACHED -> "container's capacity boundaries reached";
            case DUPLICATE_BOUNDED_RESOURCE -> "resource type is already bound to another shelf";
        };

        cli.reloadController(String.format(event.isAdded() ? "You cannot add%s into container: %s." : "You cannot remove%s from container: %s.",
                event.getResType() == null ? "" : String.format(" %s", event.getResType()),
                reason));
    }

    @Override
    public void on(ErrServerUnavailable event) {
        super.on(event);

        if (!cli.getUi().hasClientOpen())
            return;

        cli.getUi().closeClient();

        cli.alert("Server is down. Try again later.");
        cli.setReturningToMainMenu();
    }

    @Override
    public void on(ResQuit event) {
        super.on(event);

        cli.setController(new MainMenuController());
    }

    @Override
    public void on(UpdateActionToken event) {
        super.on(event);

        /* Print only, no cache update */
        vm.getActionToken(event.getActionToken())
                .ifPresent(t -> {
                    cli.getOut().println();
                    new ActionToken(t).render();
                });
    }

    @Override
    public void on(UpdateCurrentPlayer event) {
        super.on(event);

        /* DO NOT DO STATE SWITCHING HERE */

        if (!vm.localPlayerIsCurrent()) {
            cli.getOut().println();
            cli.getOut().println(center(String.format("Current player: %s", event.getPlayer())));
        }
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
        new DevSlots(event.getPlayer(), vm.getPlayerDevelopmentSlots(event.getPlayer())).render();
    }

    @Override
    public void on(UpdateFaithPoints event) {
        super.on(event);

        cli.getOut().println();
        vm.getFaithTrack().ifPresent(faithTrack -> {
            cli.getOut().println();
            new FaithTrack(faithTrack, vm.getPlayersFaithPoints()).render();
        });
    }

    @Override
    public void on(UpdateGame event) {
        super.on(event);

        vm.getDevCardGrid().ifPresent(devCardGrid -> {
            cli.getOut().println();
            cli.getOut().println();
            new DevCardGrid(devCardGrid).render();
        });

        vm.getMarket().ifPresent(market -> {
            cli.getOut().println();
            cli.getOut().println();
            new Market(market).render();
        });

        vm.getLocalPlayer().ifPresent(player -> {
            cli.getOut().println();
            cli.getOut().println();
            new ResourceContainerSet(
                    player,
                    vm.getPlayerWarehouseShelves(player),
                    vm.getPlayerDepots(player),
                    vm.getPlayerStrongbox(player).orElse(null)).render();
        });

        vm.getLocalPlayer().map(vm::getPlayerLeaderCards).ifPresent(leaderCards -> {
            cli.getOut().println();
            cli.getOut().println();
            new LeadersHand(leaderCards).render();
        });

        vm.getLocalPlayer().ifPresent(player -> {
            cli.getOut().println();
            cli.getOut().println();
            new DevSlots(player, vm.getPlayerDevelopmentSlots(player)).render();
        });

        cli.getOut().println();
        cli.getOut().println();
        cli.getOut().println(center("Players:" + "\n" + "\n" + new UnorderedList(event.getPlayers().stream()
                .map(ReducedPlayer::getNickname)
                .map(nickname -> vm.getAnsiPlayerColor(nickname).map(ansiColor -> boldColor(nickname, ansiColor)).orElse(nickname))
                .toList()).getString()));

        vm.getInkwellPlayer().ifPresent(player -> {
            cli.getOut().println();
            cli.getOut().println();
            cli.getOut().println(center(String.format("%s has the inkwell.", player)));
        });

        vm.getWinnerPlayer().ifPresent(player -> {
            cli.getOut().println();
            cli.getOut().println();
            cli.getOut().println(center(String.format("%s won the game!", event.getWinnerPlayer())));
        });

        if (vm.isLastRound()) {
            cli.getOut().println();
            cli.getOut().println();
            cli.getOut().println(center("Last round!"));
        }

        if (vm.isGameEnded()) {
            cli.getOut().println();
            cli.getOut().println();
            cli.getOut().println(center("Game ended!"));
        }

        vm.getFaithTrack().ifPresent(faithTrack -> {
            cli.getOut().println();
            cli.getOut().println();
            new FaithTrack(faithTrack, vm.getPlayersFaithPoints()).render();
        });
    }

    @Override
    public void on(UpdateGameEnd event) {
        super.on(event);

        cli.getOut().println();
        cli.getOut().println(center("Game ended!"));
        cli.setController(new EndgameController());
    }

    @Override
    public void on(UpdateLastRound event) {
        super.on(event);

        cli.getOut().println();
        cli.getOut().println(center("Last round!"));
    }

    @Override
    public void on(UpdateActivateLeader event) {
        super.on(event);

        vm.getCurrentPlayer().ifPresent(player -> {
            cli.getOut().println();
            cli.getOut().println(center(String.format("%s activated leader card %d:", player, event.getLeader())));
        });

        vm.getLeaderCard(event.getLeader()).ifPresent(leaderCard -> {
            cli.getOut().println();
            new Box(new LeaderCard(leaderCard)).render();
        });
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
        cli.getOut().println(center(String.format("Player %s now has %d leader cards.", event.getPlayer(), event.getLeadersHandCount())));
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
        cli.getOut().println(center(String.format("Player %s %s.", event.getPlayer(), event.isActive() ? "reconnected" : "disconnected")));
    }

    @Override
    public void on(UpdateResourceContainer event) {
        super.on(event);

        cli.getOut().println();
        cli.getOut().println(center(String.format("%s's resource container has been updated:", vm.getCurrentPlayer().orElseThrow())));
        cli.getOut().println();
        new Box(new ResourceContainer(event.getResContainer())).render();
    }

    @Override
    public void on(UpdateVaticanSection event) {
        super.on(event);

        cli.getOut().println();
        cli.getOut().println(center(String.format("Activated vatican section %d", event.getVaticanSection() + 1)));

        vm.getFaithTrack().ifPresent(faithTrack -> {
            cli.getOut().println();
            new FaithTrack(faithTrack, vm.getPlayersFaithPoints()).render();
        });
    }

    @Override
    public void on(UpdateVictoryPoints event) {
        super.on(event);

        cli.getOut().println();
        new LeaderBoard().render();
    }
}
