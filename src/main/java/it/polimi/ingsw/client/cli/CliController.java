package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.UiController;
import it.polimi.ingsw.client.cli.components.*;
import it.polimi.ingsw.client.viewmodel.PlayerData;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.common.reducedmodel.ReducedFaithTrack;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static it.polimi.ingsw.client.cli.Cli.center;

public abstract class CliController extends UiController implements Renderable {
    private final Cli cli = Cli.getInstance();
    
    public CliController() {
        super(Cli.getInstance().getUi());
    }
    
    @Override
    public abstract void render(Cli cli);

    // TODO review all error states

    @Override
    public void on(ErrAction event) {
        ViewModel vm = cli.getViewModel();

        // TODO: handle
        // switch (event.getReason()) {
        // case LATE_SETUP_ACTION:
        //     if ()
        // break;
        // case EARLY_MANDATORY_ACTION:

        // break;
        // case LATE_MANDATORY_ACTION:

        // break;
        // case EARLY_TURN_END:

        // break;
        // case GAME_ENDED:

        // break;
        // case NOT_CURRENT_PLAYER:

        // break;
        // default:
        //         break;
        // }
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
        super.on(event);
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
    }

    @Override
    public void on(ErrNoSuchEntity event) {
        super.on(event);
        
        cli.repeatState(
            String.format("No such entity %s: ID %d, code %s.",
                event.getOriginalEntity().toString().toLowerCase(),
                event.getId(),
                event.getCode()));
    }

    @Override
    public void on(ErrObjectNotOwned event) {
        super.on(event);
        
        cli.repeatState(String.format("%s with ID %d isn't yours. Are you sure you typed that right?", event.getObjectType(), event.getId()));
    }

    @Override
    public void on(ErrReplacedTransRecipe event) {
        super.on(event);
        
        cli.repeatState(
                String.format(
                        "Discrepancy in the transaction recipe.\nResource: %s, replaced count: %d, specified shelfmap count: %d",
                        event.getResType(), event.getReplacedCount(), event.getShelvesChoiceResCount()));
    }

    @Override
    public void on(ErrResourceReplacement event) {
        super.on(event);
        
        if (event.isExcluded() || event.isNonStorable())
            cli.repeatState(String.format("Error validating transaction request %s: %s resource found.",
                    event.isInput() ? "input" : "output",
                    event.isNonStorable() ? "nonstorable" : "excluded"));
        else
            cli.repeatState(String.format("Error validating transaction request: %d replaced resources when %d replaceable.",
                    event.getReplacedCount(),
                    event.getBlanks()));
    }

    @Override
    public void on(ErrResourceTransfer event) {
        super.on(event);
        
        cli.repeatState(String.format("Error transferring resources: resource %s, %s, %s.",
                event.getResType(),
                event.isAdded() ? "added" : "removed",
                event.getReason().toString()));
    }

    @Override
    public void on(ResQuit event) {
        super.on(event);
        
        cli.getUi().closeClient();
        cli.setState(new MainMenuState());
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
        cli.getViewModel()
                .getActionToken(event.getActionToken())
                .ifPresent(t -> {
                    cli.getOut().println();
                    new ActionToken(t).render(cli);
                });
        cli.promptPause();
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
        new DevCardGrid(cli.getViewModel().getDevCardGrid().orElseThrow()).render(cli);
    }

    @Override
    public void on(UpdateDevCardSlot event) {
        super.on(event);
        
        new DevSlots(cli.getViewModel()
                .getPlayerDevelopmentSlots(cli.getViewModel().getLocalPlayerNickname())).render(cli);
    }

    @Override
    public void on(UpdateFaithPoints event) {
        super.on(event);
        
        ViewModel vm = cli.getViewModel();

        if (event.isBlackCross())
            vm.setBlackCrossFP(event.getFaithPoints());
        else
            vm.getPlayerData(event.getPlayer()).orElseThrow().setFaithPoints(event.getFaithPoints());
        
        Map<String, Integer> points = vm.getPlayerNicknames().stream()
            .collect(Collectors.toMap(n -> n, n -> vm.getPlayerFaithPoints(n)));

        Optional<ReducedFaithTrack> ft = vm.getFaithTrack();
        if (ft.isPresent()) {
            cli.getOut().println();
            new FaithTrack(ft.get(), points).render(cli);
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
    
        ViewModel vm = cli.getViewModel();

        vm.setActionTokens(event.getActionTokens());
        vm.setContainers(event.getResContainers());
        vm.setDevelopmentCards(event.getDevelopmentCards());
        vm.setLeaderCards(event.getLeaderCards());
        vm.setPlayerNicknames(event.getPlayers());
        vm.setProductions(event.getProductions());
        vm.setFaithTrack(event.getFaithTrack());
        vm.setDevCardColors(event.getColors());
        vm.setResourceTypes(event.getResourceTypes());
        vm.setResumedGame(event.isResumed());
        vm.setSlotsCount(event.getSlotsCount());

        Map<String, Integer> points = vm.getPlayerNicknames().stream()
            .collect(Collectors.toMap(nick -> nick, nick -> 0));
        cli.getOut().println();
        new FaithTrack(cli.getViewModel().getFaithTrack().orElseThrow(), points).render(cli);
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
        
        ViewModel vm = cli.getViewModel();

        vm.activateLeaderCard(event.getLeader());
        
        cli.getOut().println();
        cli.getOut().printf("%s activated leader card %d.%n", vm.getCurrentPlayer(), event.getLeader());
        cli.getOut().println();
        new LeaderCard(vm.getLeaderCard(event.getLeader()).orElseThrow()).render(cli);
    }

    @Override
    public void on(UpdateLeadersHand event) {
        super.on(event);
        
        /* this message arrives last among the starting events:
            joingame
            updategame
            currplayer
            market
            devcardgrid
            player
            leadershand -> client has enough info for leader choice */

        super.on(event);
        cli.getOut().println();
        cli.getOut().println(center(String.format("%s's leader cards:", event.getPlayer())));
        cli.getOut().println();
        new LeadersHand(cli.getViewModel().getPlayerLeaderCards(event.getPlayer())).render(cli);
    }

    @Override
    public void on(UpdateLeadersHandCount event) {
        super.on(event);
        
        cli.getOut().println();
        cli.getOut().println(center(String.format("Player %s now has %d leader cards.", event.getPlayer(), event.getLeadersCount())));
        cli.promptPause();
    }

    @Override
    public void on(UpdateMarket event) {
        super.on(event);
        
        cli.getOut().println();
        new Market(event.getMarket()).render(cli);
    }

    @Override
    public void on(UpdatePlayer event) {
        super.on(event);
        
        ViewModel vm = cli.getViewModel();
        vm.setPlayerData(event.getPlayer(), new PlayerData(
                event.getBaseProduction(),
                event.getPlayerSetup(),
                event.getStrongbox(),
                event.getWarehouseShelves()));
        cli.getOut().println();
        
        new ResourceContainers(
                event.getPlayer(),
                vm.getPlayerWarehouseShelves(event.getPlayer()),
                vm.getPlayerDepots(event.getPlayer()),
                vm.getPlayerStrongbox(event.getPlayer()).orElse(null))
                .render(cli);

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
        
        if (cli.getViewModel().getPlayerNicknames().size() > 1) {
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
        cli.getOut().printf("Victory points for %s: %d.%n", event.getPlayer(), event.getVictoryPoints());
    }
}
