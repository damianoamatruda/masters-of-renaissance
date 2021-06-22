package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.UiController;
import it.polimi.ingsw.client.cli.components.*;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.common.events.mvevents.errors.ErrNickname.ErrNicknameReason;
import it.polimi.ingsw.common.reducedmodel.ReducedFaithTrack;
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
            if (isSetupDone) { // setup is done
                if (vm.getCurrentPlayer().equals(vm.getLocalPlayerNickname()))
                    cli.setController(new TurnBeforeActionState());
                else
                    cli.setController(new WaitingAfterTurnState());
            } else // setup not done
                vm.getPlayerData(vm.getLocalPlayerNickname()).ifPresent(pd -> {
                    pd.getSetup().ifPresent(setup -> { // received local player's setup
                        if (isLeaderSetupAvailable())
                            cli.setController(new SetupLeadersState());
                        else if (isResourceSetupAvailable())
                            cli.setController(new SetupResourcesState());
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
        case LATE_SETUP_ACTION:
            new Thread(() -> {
                cli.getOut().println(
                    center("Setup phase is concluded, advancing to game turns."));
                cli.promptPause();
            
                if (vm.getCurrentPlayer().equals(vm.getLocalPlayerNickname()))
                    cli.setController(new TurnBeforeActionState());
                else
                    cli.setController(new WaitingAfterTurnState());
            }).start();
        break;
        case EARLY_MANDATORY_ACTION:
            new Thread(() -> {
                cli.getOut().println(
                    center("A mandatory action is trying to be executed before the setup phase is concluded, returning to setup phase."));
                cli.promptPause();
            
                if (!isLocalLeaderSetupDone())
                    cli.setController(new SetupLeadersState());
                else if (!isLocalResourceSetupDone())
                    cli.setController(new SetupResourcesState());
                else
                    setNextState();
            }).start();
        break;
        case LATE_MANDATORY_ACTION:
            new Thread(() -> {
                cli.getOut().println(
                    center("A mandatory action has already been executed, advancing to optional actions."));
                cli.promptPause();

                cli.setController(new TurnAfterActionState());
            }).start();
        break;
        case EARLY_TURN_END:
            new Thread(() -> {
                cli.getOut().println(
                    center("A mandatory action needs to be executed before ending the turn."));
                cli.promptPause();

                cli.setController(new TurnBeforeActionState());
            }).start();
            break;
        case GAME_ENDED:
            new Thread(() -> {
                cli.getOut().println(
                    center("The match is finished, advancing to ending screen."));
                cli.promptPause();

                cli.setController(new GameEndState());
            }).start();
        break;
        case NOT_CURRENT_PLAYER:
            new Thread(() -> {
                cli.getOut().println(
                    center("You are not the current player. Please wait for your turn."));
                cli.promptPause();
                    
                cli.setController(new WaitingAfterTurnState());
            }).start();
        break;
        default:
            cli.getOut().println(center(String.format("Unsupported ErrAction reason %s.", event.getReason().toString())));
        break;
        }
    }

    @Override
    public void on(ErrBuyDevCard event) {
        super.on(event);
    }

    @Override
    public void on(ErrCardRequirements event) {
        String msg = "\nUnsatisfied card requirements:\n";
        if (event.getMissingDevCards().isPresent()) {
            msg = msg.concat("Missing development cards:\n");
    
            for (ReducedDevCardRequirementEntry e : event.getMissingDevCards().get())
                msg = msg.concat(String.format("Color: %s, level: %d, missing: %d\n", e.getColor(), e.getLevel(), e.getAmount()));
        } else {
            msg = msg.concat("Missing resources:\n");

            for (Map.Entry<String, Integer> e : event.getMissingResources().get().entrySet())
                msg = msg.concat(String.format("Resource type: %s, missing %d\n", e.getKey(), e.getValue()));
        }

        cli.reloadController(msg);
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

        if (event.getReason() == ErrNicknameReason.NOT_IN_GAME) {
            new Thread(() -> {
                cli.getOut().println("Match not joined yet.");
                cli.promptPause();
                cli.setController(new InputNicknameState(""));
            }).start();
            return;
        }

        String reason = "";
        switch (event.getReason()) {
            case ALREADY_SET:
            case TAKEN:
            reason = String.format("nickname is %s.", event.getReason().toString().toLowerCase().replace('_', ' '));
            break;
            case NOT_SET:
                reason = "nickname is blank.";
            break;
            default:
                reason = "unsupported ErrNickname option";
            break;
        }
        cli.reloadController(String.format("Error setting nickname: %s", reason));
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
                    "Irregular amount of %s specified in the container map: %d specified, %d required.",
                    event.getResType().isEmpty() ? "resources" : event.getResType(),
                    event.getReplacedCount(),
                    event.getShelvesChoiceResCount()));
    }

    @Override
    public void on(ErrResourceReplacement event) {
        super.on(event);
        
        cli.reloadController(String.format("Error validating transaction request %s: %s resource found.",
                event.isInput() ? "input" : "output",
                event.isNonStorable() ? "nonstorable" : "excluded"));
    }

    @Override
    public void on(ErrResourceTransfer event) {
        super.on(event);

        String reason = "";

        switch (event.getReason()) {
            case BOUNDED_RESTYPE_DIFFER:
                reason = "shelf's binding resource type is different from transferring resource";
            break;
            case NON_STORABLE:
                reason = "resource type is not storable";
            break;
            case CAPACITY_REACHED:
                reason = "shelf's capacity boundaries reached";
            break;
            case DUPLICATE_BOUNDED_RESOURCE:
                reason = "resource type is already bound to another shelf";
            break;
            default:
                reason = "unsupported ErrResourceTransfer option";
                break;
        }

        cli.reloadController(String.format("Error %s resource %s from container: %s.",
                event.isAdded() ? "adding" : "removing",
                event.getResType(),
                reason));
    }

    @Override
    public void on(ErrServerUnavailable event) {
        new Thread(() -> {
            cli.getOut().println("Server disconnected, returning to main menu");
            cli.promptPause();
            cli.setController(new MainMenuState());
        }).start();
    }

    @Override
    public void on(ResQuit event) {
        super.on(event);
        
        cli.setController(new MainMenuState());
    }

    @Override
    public void on(UpdateAction event) {
        super.on(event);
        
        // TODO: IMPLEMENT IN STATES
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
        new Thread(cli::promptPause).start();
    }

    @Override
    public void on(UpdateBookedSeats event) {
        super.on(event);
    }

    @Override
    public void on(UpdateCurrentPlayer event) {
        super.on(event);
        
        cli.getOut().println();
        cli.getOut().println(center(String.format("Current player: %s", event.getPlayer())));
    }

    @Override
    public void on(UpdateDevCardGrid event) {
        super.on(event);
        
        cli.getOut().println();
        new DevCardGrid(vm.getDevCardGrid().orElseThrow()).render();
    }

    @Override
    public void on(UpdateDevCardSlot event) {
        super.on(event);
        
        new DevSlots(vm.getPlayerDevelopmentSlots(vm.getLocalPlayerNickname())).render();
    }

    @Override
    public void on(UpdateFaithPoints event) {
        super.on(event);

        Map<String, Integer> points = vm.getPlayerNicknames().stream()
            .collect(Collectors.toMap(n -> n, n -> vm.getPlayerFaithPoints(n)));

        Optional<ReducedFaithTrack> ft = vm.getFaithTrack();
        if (ft.isPresent()) {
            cli.getOut().println();
            new FaithTrack(ft.get(), points).render();
        }
    }

    @Override
    public void on(UpdateGameEnd event) {
        super.on(event);
        
        cli.getOut().println();
        cli.getOut().println("Game ended!");
    }

    @Override
    public void on(UpdateGame event) {
        super.on(event);

        Map<String, Integer> points = vm.getPlayerNicknames().stream()
            .collect(Collectors.toMap(nick -> nick, nick -> 0));
        cli.getOut().println();
        new FaithTrack(vm.getFaithTrack().orElseThrow(), points).render();
    }

    @Override
    public void on(UpdateJoinGame event) {
        super.on(event);
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
        cli.getOut().println(Cli.center(String.format("%s activated leader card %d.\n", vm.getCurrentPlayer(), event.getLeader())));
        cli.getOut().println();
        cli.getOut().println(Cli.center(new Box(new LeaderCard(vm.getLeaderCard(event.getLeader()).orElseThrow())).getString(cli)));
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

        baseProds = vm.getPlayerBaseProduction(vm.getLocalPlayerNickname());
        // TODO put baseProd outside of PlayerData, since it is unique (?)

        cli.getOut().println(center("\nBase Production:"));
        cli.getOut().println(center(new Box(new ResourceTransactionRecipe(baseProds.orElseThrow()), -1).getString(cli)));
    }

    @Override
    public void on(UpdatePlayerStatus event) {
        super.on(event);
        
        cli.getOut().println();
        cli.getOut().printf("Player %s became %s.%n", event.getPlayer(), event.isActive() ? "active" : "inactive");
    }

    @Override
    public void on(UpdateResourceContainer event) {
        super.on(event);
        
        cli.getOut().println();
        cli.getOut().println(center(new Box(new ResourceContainer(event.getResContainer())).getString(cli)));
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
