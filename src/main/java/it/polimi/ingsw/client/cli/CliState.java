package it.polimi.ingsw.client.cli;

import java.util.Optional;

import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.common.events.vcevents.ReqNewGame;
import it.polimi.ingsw.common.reducedmodel.ReducedVaticanSection;

public abstract class CliState implements Renderable {
    protected void renderMainTitle(Cli cli) {
        cli.getOut().print(Cli.center(Cli.convertStreamToString(CliState.class.getResourceAsStream("/assets/cli/title.txt"))));
    }

    @Override
    public abstract void render(Cli cli);

    public void on(Cli cli, ErrAction event) {
        cli.repeatState(event.getReason().toString());
    }

    public void on(Cli cli, ErrActiveLeaderDiscarded event) {
    }

    public void on(Cli cli, ErrBuyDevCard event) {
        cli.repeatState(event.isStackEmpty() ?
                "Cannot buy development card. Deck is empty." :
                "Cannot place devcard in slot, level mismatch.");
    }

    public void on(Cli cli, ErrCardRequirements event) {
        cli.repeatState(event.getReason());
    }

    public void on(Cli cli, ErrNoSuchEntity event) {
        // TODO handle
        // cli.repeatState(event.getReason());
    }

    public void on(Cli cli, ErrInitialChoice event) {
        cli.repeatState(event.isLeadersChoice() ? // if the error is from the initial leaders choice
                event.getMissingLeadersCount() == 0 ?
                        "Leaders already chosen" :        // if the count is zero it means the leaders were already chosen
                        String.format("Not enough leaders chosen: %d missing.", event.getMissingLeadersCount()) :
                "Resources already chosen");          // else it's from the resources choice
    }

    public void on(Cli cli, ErrNewGame event) {
        cli.repeatState(event.isInvalidPlayersCount() ?
                "Invalid players count." :
                "You are not supposed to choose the players count for the game.");
    }

    public void on(Cli cli, ErrNickname event) {
        cli.repeatState("Nickname is invalid. Reason: " + event.getReason().toString().toLowerCase());
    }

    public void on(Cli cli, ErrObjectNotOwned event) {
        cli.repeatState(String.format("%s %d isn't yours. Are you sure you typed that right?", event.getObjectType(), event.getId()));
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
        cli.quit();
    }

    public void on(Cli cli, UpdateAction event) {
        // TODO: implement
    }

    public void on(Cli cli, UpdateActionToken event) {
        // TODO: Only the cache should be updated here
        cli.getPrinter().update(cli.getCache().getActionToken(event.getActionToken()));
    }

    public void on(Cli cli, UpdateBookedSeats event) {
        if (event.canPrepareNewGame().equals(cli.getCache().getNickname())) {
            if (cli.isSingleplayer())
                cli.dispatch(new ReqNewGame(1));
            else
                cli.setState(new InputPlayersCountState());
        } else
            cli.setState(new WaitingBeforeGameState(event.getBookedSeats()));
    }

    public void on(Cli cli, UpdateCurrentPlayer event) {
        cli.getCache().setCurrentPlayer(event.getPlayer());
        if (event.getPlayer().equals(cli.getCache().getNickname()))
            cli.setState(new TurnBeforeActionState());
        else
            cli.setState(new WaitingAfterTurnState());
    }

    public void on(Cli cli, UpdateDevCardGrid event) {
        cli.getCache().setDevCardGrid(event.getCards());
    }

    public void on(Cli cli, UpdateDevCardSlot event) {
        cli.getCache().setPlayerDevSlot(cli.getCache().getCurrentPlayer(), event.getDevSlot(), event.getDevCard());
    }

    public void on(Cli cli, UpdateFaithPoints event) {
        cli.getCache().setFaithPoints(event.getPlayer(), event.getFaithPoints());
    }

    public void on(Cli cli, UpdateGameEnd event) {
        cli.getCache().setWinner(event.getWinner());
        cli.setState(new GameEndState());
    }

    public void on(Cli cli, UpdateGame event) {
        cli.getCache().setActionTokens(event.getActionTokens());
        cli.getCache().setContainers(event.getResContainers());
        cli.getCache().setDevelopmentCards(event.getDevelopmentCards());
        cli.getCache().setLeaderCards(event.getLeaderCards());
        cli.getCache().setFaithTrack(event.getFaithTrack());
        event.getPlayers().stream().forEach(p-> cli.getCache().setFaithPoints(p, 0));
        cli.getCache().setProductions(event.getProductions());
        cli.getCache().setColors(event.getColors());
        cli.getCache().setResourceTypes(event.getResourceTypes());

        if (!event.isResumed())
            event.getPlayers().forEach(p -> cli.getCache().setVictoryPoints(p, 0));
        else
            cli.setState(new WaitingAfterTurnState());
    }

    public void on(Cli cli, UpdateJoinGame event) {
        cli.setState(new WaitingBeforeGameState(event.getPlayersCount()));
    }

    public void on(Cli cli, UpdateLastRound event) {
        cli.getCache().setLastRound();
    }

    public void on(Cli cli, UpdateLeader event) {
        if (event.isActive())
            cli.getCache().setPlayerLeaders(cli.getCache().getCurrentPlayer(), event.getLeader());
    }

    public void on(Cli cli, UpdateLeadersHand event) {
        /* this message arrives last among the starting events:
            joingame
            updategamestart
            currplayer
            market
            devcardgrid
            player
            leadershand -> with GS and player has enough info for leader choice */

        event.getLeaders().forEach(id -> cli.getCache().setPlayerLeaders(event.getPlayer(), id));

        // TODO: Completely refactor this, splitting it into the right states

        if (cli.getCache().getSetup(cli.getCache().getNickname()) != null &&
                event.getLeaders().size() > cli.getCache().getSetup(cli.getCache().getNickname()).getChosenLeadersCount()) {
            cli.setState(new SetupLeadersState(
                    event.getLeaders().size() - cli.getCache().getSetup(cli.getCache().getNickname()).getChosenLeadersCount()));
        } else if (cli.getCache().getSetup(cli.getCache().getNickname()) != null &&
                cli.getCache().getSetup(cli.getCache().getNickname()).getInitialResources() > 0 &&
                !(cli.getState() instanceof SetupResourcesState)) {
            cli.setState(new SetupResourcesState(
                    cli.getCache().getSetup(cli.getCache().getNickname()).getInitialResources()));
        } else {
            if (!(cli.getState() instanceof TurnBeforeActionState))
                cli.setState(new TurnBeforeActionState());
        }
    }

    public void on(Cli cli, UpdateLeadersHandCount event) {
        cli.getCache().setPlayerLeadersCount(event.getPlayer(), event.getLeadersCount());
    }

    public void on(Cli cli, UpdateMarket event) {
        cli.getCache().setMarket(event.getMarket());
    }

    public void on(Cli cli, UpdatePlayer event) {
        cli.getCache().setBaseProduction(event.getPlayer(), event.getBaseProduction());
        cli.getCache().setPlayerWarehouseShelves(event.getPlayer(), event.getWarehouseShelves());
        cli.getCache().setPlayerStrongbox(event.getPlayer(), event.getStrongbox());
        cli.getCache().setSetup(event.getPlayer(), event.getPlayerSetup());
    }

    public void on(Cli cli, UpdatePlayerStatus event) {
        cli.getCache().setPlayerState(event.getPlayer(), event.isActive());
    }

    public void on(Cli cli, UpdateResourceContainer event) {
        cli.getCache().setContainer(event.getResContainer());
    }

    public void on(Cli cli, UpdateSetupDone event) {
        cli.setState(new TurnBeforeActionState());
    }

    public void on(Cli cli, UpdateVaticanSection event) {
        Optional<ReducedVaticanSection> vs = cli.getCache().getFaithTrack().getVaticanSections().entrySet().stream()
            .filter(e -> e.getValue().getId() == event.getVaticanSection()).map(e -> e.getValue()).findAny();
        
        vs.ifPresent(s -> s.setActive());
    }

    public void on(Cli cli, UpdateVictoryPoints event) {
        cli.getCache().setVictoryPoints(event.getPlayer(), event.getVictoryPoints());
    }
}
