package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.*;
import it.polimi.ingsw.client.viewmodel.PlayerData;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.common.reducedmodel.ReducedFaithTrack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class CliState implements Renderable {
    @Override
    public abstract void render(Cli cli);

    // TODO review all error states

    public void on(Cli cli, ErrAction event) {
    }

    public void on(Cli cli, ErrActiveLeaderDiscarded event) {
    }

    public void on(Cli cli, ErrBuyDevCard event) {
    }

    public void on(Cli cli, ErrCardRequirements event) {
    }

    public void on(Cli cli, ErrInitialChoice event) {
    }

    public void on(Cli cli, ErrNewGame event) {
    }

    public void on(Cli cli, ErrNickname event) {
    }

    public void on(Cli cli, ErrNoSuchEntity event) {
        cli.repeatState(
            String.format("No such entity %s: ID %d, code %s.",
                event.getOriginalEntity().toString().toLowerCase(),
                event.getId(),
                event.getCode()));
    }

    public void on(Cli cli, ErrObjectNotOwned event) {
        cli.repeatState(String.format("%s with ID %d isn't yours. Are you sure you typed that right?", event.getObjectType(), event.getId()));
    }

    public void on(Cli cli, ErrReplacedTransRecipe event) {
        cli.repeatState(
                String.format(
                        "Discrepancy in the transaction recipe.\nResource: %s, replaced count: %d, specified shelfmap count: %d",
                        event.getResType(), event.getReplacedCount(), event.getShelvesChoiceResCount()));
    }

    public void on(Cli cli, ErrResourceReplacement event) {
        if (event.isExcluded() || event.isNonStorable())
            cli.repeatState(String.format("Error validating transaction request %s: %s resource found.",
                    event.isInput() ? "input" : "output",
                    event.isNonStorable() ? "nonstorable" : "excluded"));
        else
            cli.repeatState(String.format("Error validating transaction request: %d replaced resources when %d replaceable.",
                    event.getReplacedCount(),
                    event.getBlanks()));
    }

    public void on(Cli cli, ErrResourceTransfer event) {
        cli.repeatState(String.format("Error transferring resources: resource %s, %s, %s.",
                event.getResType(),
                event.isAdded() ? "added" : "removed",
                event.getReason().toString()));
    }

    public void on(Cli cli, ResQuit event) {
        cli.stopNetwork();
        cli.setState(new MainMenuState());
    }

    public void on(Cli cli, UpdateAction event) {
        // TODO: IMPLEMENT IN STATES
    }

    public void on(Cli cli, UpdateActionToken event) {
        // print only, no cache update
        cli.getViewModel()
                .getActionToken(event.getActionToken())
                .ifPresent(t -> {
                    cli.getOut().println();
                    new ActionToken(t).render(cli);
                });
        cli.getOut().println();
        cli.promptPause();
    }

    public void on(Cli cli, UpdateBookedSeats event) {
    }

    public void on(Cli cli, UpdateCurrentPlayer event) {
        cli.getViewModel().setCurrentPlayer(event.getPlayer());
        cli.getOut().println();
        cli.getOut().print(Cli.center(String.format("Current player: %s", event.getPlayer())));
    }

    public void on(Cli cli, UpdateDevCardGrid event) {
        cli.getViewModel().setDevCardGrid(event.getCards());
        cli.getOut().println();
        new DevCardGrid(cli.getViewModel().getDevCardGrid().orElseThrow()).render(cli);
    }

    public void on(Cli cli, UpdateDevCardSlot event) {
        cli.getViewModel().getCurrentPlayerData().orElseThrow().setDevSlot(event.getDevSlot(), event.getDevCard());
        // TODO: print
    }

    public void on(Cli cli, UpdateFaithPoints event) {
        ViewModel vm = cli.getViewModel();

        if (event.isBlackCross())
            vm.setBlackCrossFP(event.getFaithPoints());
        else
            vm.getPlayerData(event.getPlayer()).orElseThrow().setFaithPoints(event.getFaithPoints());
        
        Map<String, Integer> points = vm.getPlayerNicknames().stream()
            .collect(Collectors.toMap(nick -> nick, nick -> {
                Optional<PlayerData> pd = vm.getPlayerData(nick);

                return pd.map(PlayerData::getFaithPoints).orElse(0);

            }));

        Optional<ReducedFaithTrack> ft = vm.getFaithTrack();
        if (ft.isPresent()) {
            cli.getOut().println();
            new FaithTrack(ft.get(), points).render(cli);
        }
    }

    public void on(Cli cli, UpdateGameEnd event) {
        cli.getViewModel().setWinner(event.getWinner());
        cli.getOut().println();
        cli.getOut().println("Game ended!");
    }

    public void on(Cli cli, UpdateGame event) {
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

        Map<String, Integer> points = vm.getPlayerNicknames().stream()
            .collect(Collectors.toMap(nick -> nick, nick -> 0));
        cli.getOut().println();
        new FaithTrack(cli.getViewModel().getFaithTrack().orElseThrow(), points).render(cli);
    }

    public void on(Cli cli, UpdateJoinGame event) {
    }

    public void on(Cli cli, UpdateLastRound event) {
        cli.getViewModel().setLastRound();
        cli.getOut().println();
        cli.getOut().println("Last round!");
    }

    public void on(Cli cli, UpdateActivateLeader event) {
        ViewModel vm = cli.getViewModel();

        vm.activateLeaderCard(event.getLeader());
        
        cli.getOut().println();
        cli.getOut().printf("%s activated leader card %d.%n", vm.getCurrentPlayer(), event.getLeader());
        cli.getOut().println();
        new LeaderCard(vm.getLeaderCard(event.getLeader()).orElseThrow()).render(cli);
    }

    public void on(Cli cli, UpdateLeadersHand event) {
        /* this message arrives last among the starting events:
            joingame
            updategame
            currplayer
            market
            devcardgrid
            player
            leadershand -> client has enough info for leader choice */

        cli.getViewModel().getPlayerData(event.getPlayer()).orElseThrow().setLeadersHand(event.getLeaders());
        cli.getOut().println();
        cli.getOut().printf("%s's leader cards:%n", event.getPlayer());
        cli.getOut().println();
        new LeadersHand(cli.getViewModel().getPlayerLeaderCards(event.getPlayer())).render(cli);
    }

    public void on(Cli cli, UpdateLeadersHandCount event) {
        cli.getViewModel().getPlayerData(event.getPlayer()).orElseThrow().setLeadersCount(event.getLeadersCount());
        cli.getOut().println();
        cli.getOut().print(Cli.center(String.format("Player %s now has %d leader cards.", event.getPlayer(), event.getLeadersCount())));
    }

    public void on(Cli cli, UpdateMarket event) {
        cli.getViewModel().setMarket(event.getMarket());
        cli.getOut().println();
        new Market(event.getMarket()).render(cli);
    }

    public void on(Cli cli, UpdatePlayer event) {
        ViewModel vm = cli.getViewModel();
        vm.setPlayerData(event.getPlayer(), new PlayerData(
                event.getBaseProduction(),
                event.getPlayerSetup(),
                event.getStrongbox(),
                event.getWarehouseShelves()));
        cli.getOut().println();
        cli.showContainers(event.getPlayer());

        Map<String, Integer> baseProds = new HashMap<>();

        baseProds = vm.getPlayerNicknames().stream().filter(nick -> vm.getPlayerData(nick).isPresent()).collect(Collectors.toMap(n -> n, n -> vm.getPlayerData(n).get().getBaseProduction()));

        cli.getOut().println(new BaseProductions(baseProds).getString(cli));
    }

    public void on(Cli cli, UpdatePlayerStatus event) {
        cli.getViewModel().getPlayerData(event.getPlayer()).orElseThrow().setActive(event.isActive());
        cli.getOut().println();
        cli.getOut().printf("Player %s became %s.%n", event.getPlayer(), event.isActive() ? "active" : "inactive");
    }

    public void on(Cli cli, UpdateResourceContainer event) {
        cli.getViewModel().setContainer(event.getResContainer());
        cli.getOut().println();
        new ResourceContainer(event.getResContainer()).render(cli);
    }

    public void on(Cli cli, UpdateSetupDone event) {
        cli.getViewModel().setSetupDone(true);
        if (cli.getViewModel().getPlayerNicknames().size() > 1) {
            cli.getOut().println();
            cli.getOut().println("All players have finished their setup! Game starting...");
        }
    }

    public void on(Cli cli, UpdateVaticanSection event) {
        cli.getViewModel().setVaticanSection(event.getVaticanSection());
        cli.getOut().println();
        cli.getOut().printf("Activated vatican section %d%n", event.getVaticanSection()); // TODO: improve
    }

    public void on(Cli cli, UpdateVictoryPoints event) {
        cli.getViewModel().getPlayerData(event.getPlayer()).orElseThrow().setVictoryPoints(event.getVictoryPoints());
        cli.getOut().println();
        cli.getOut().printf("Victory points for %s: %d.%n", event.getPlayer(), event.getVictoryPoints());
    }
}
