package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;
import it.polimi.ingsw.client.ViewModel.PlayerData;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;

public abstract class CliState implements Renderable {
    protected void renderMainTitle(Cli cli) {
        cli.getOut().print(Cli.center(Cli.convertStreamToString(CliState.class.getResourceAsStream("/assets/cli/title.txt"))));
    }

    @Override
    public abstract void render(Cli cli);

    // TODO review all error states
    public void on(Cli cli, ErrAction event) {
        // handled by specific state, leave empty
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
        // cli.repeatState(event.getReason());
    }

    public void on(Cli cli, ErrInitialChoice event) {
        // repeats either SetupLeadersState or SetupResourcesState
        // if it doesn't, that's really bad
        assert(cli.getState() instanceof SetupLeadersState || cli.getState() instanceof SetupResourcesState);

        cli.repeatState(event.isLeadersChoice() ? // if the error is from the initial leaders choice
                event.getMissingLeadersCount() == 0 ?
                        "Leaders already chosen" :        // if the count is zero it means the leaders were already chosen
                        String.format("Not enough leaders chosen: %d missing.", event.getMissingLeadersCount()) :
                "Resources already chosen");          // else it's from the resources choice
    }

    public void on(Cli cli, ErrNewGame event) {
        // handled by specific state, leave empty
    }

    public void on(Cli cli, ErrNickname event) {
        // handled by specific state, leave empty
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
        cli.quit();
    }

    public void on(Cli cli, UpdateAction event) {
        // DON'T FORGET TO IMPLEMENT IN STATES
    }

    public void on(Cli cli, UpdateActionToken event) {
        // print only, no cache update
        cli.getCache().getGameData()
            .getActionToken(event.getActionToken())
            .ifPresent(t -> cli.getPrinter().update(t));
    }

    public void on(Cli cli, UpdateBookedSeats event) {
        // if (event.canPrepareNewGame().equals(cli.getCache().getUiData().getLocalPlayerNickname())) {
        //     if (cli.isSingleplayer())
        //         cli.dispatch(new ReqNewGame(1));
        //     else
        //         cli.setState(new InputPlayersCountState());
        // } else
        //     cli.setState(new WaitingBeforeGameState(event.getBookedSeats()));
    }

    public void on(Cli cli, UpdateCurrentPlayer event) {
        cli.getCache().getGameData().setCurrentPlayer(event.getPlayer());

        if (event.getPlayer().equals(cli.getCache().getUiData().getLocalPlayerNickname()))
            cli.setState(new TurnBeforeActionState());
        else
            cli.setState(new WaitingAfterTurnState());
    }

    public void on(Cli cli, UpdateDevCardGrid event) {
        cli.getCache().getGameData().setDevCardGrid(event.getCards());
    }

    public void on(Cli cli, UpdateDevCardSlot event) {
        cli.getCache().getCurrentPlayerData().setDevSlot(event.getDevSlot(), event.getDevCard());
    }

    public void on(Cli cli, UpdateFaithPoints event) {
        cli.getCache().getCurrentPlayerData().setFaithPoints(event.getFaithPoints());
    }

    public void on(Cli cli, UpdateGameEnd event) {
        cli.getCache().getGameData().setWinner(event.getWinner());
        // cli.setState(new GameEndState());
    }

    public void on(Cli cli, UpdateGame event) {
        cli.getCache().getGameData().setActionTokens(event.getActionTokens());
        cli.getCache().getGameData().setContainers(event.getResContainers());
        cli.getCache().getGameData().setDevelopmentCards(event.getDevelopmentCards());
        cli.getCache().getGameData().setLeaderCards(event.getLeaderCards());
        cli.getCache().getGameData().setPlayerNicknames(event.getPlayers());
        cli.getCache().getGameData().setProductions(event.getProductions());
        cli.getCache().getGameData().setFaithTrack(event.getFaithTrack());
        cli.getCache().getGameData().setDevCardColors(event.getColors());
        cli.getCache().getGameData().setResourceTypes(event.getResourceTypes());
    }

    public void on(Cli cli, UpdateJoinGame event) {
        // cli.setState(new WaitingBeforeGameState(event.getPlayersCount()));
    }

    public void on(Cli cli, UpdateLastRound event) {
        cli.getCache().getGameData().setLastRound();
    }

    public void on(Cli cli, UpdateLeader event) {
        if (event.isActive())
            cli.getCache().getGameData()
                .getLeaderCard(event.getLeader())
                .ifPresent(ReducedLeaderCard::setActive);
        else 
            // is a discard move (well, technically never used as such,
            // see constructor references and UpdateLeadersHandCount)
            cli.getCache().getCurrentPlayerData().setLeadersCount(
                    cli.getCache().getCurrentPlayerData().getLeadersCount() - 1);
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

        cli.getCache().getCurrentPlayerData().setLeadersHand(event.getLeaders());
        
        // TODO: Completely refactor this, splitting it into the right states
        // TODO distinguish hand based on player nickname

        // if (cli.getCache().getSetup(cli.getCache().getNickname()) != null &&
        //         event.getLeaders().size() > cli.getCache().getSetup(cli.getCache().getNickname()).getChosenLeadersCount()) {
        //     cli.setState(new SetupLeadersState(
        //             event.getLeaders().size() - cli.getCache().getSetup(cli.getCache().getNickname()).getChosenLeadersCount()));
        // } else if (cli.getCache().getSetup(cli.getCache().getNickname()) != null &&
        //         cli.getCache().getSetup(cli.getCache().getNickname()).getInitialResources() > 0 &&
        //         !(cli.getState() instanceof SetupResourcesState)) {
        //     cli.setState(new SetupResourcesState(
        //             cli.getCache().getSetup(cli.getCache().getNickname()).getInitialResources()));
        // } else {
        //     if (!(cli.getState() instanceof TurnBeforeActionState))
        //         cli.setState(new TurnBeforeActionState());
        // }
    }

    public void on(Cli cli, UpdateLeadersHandCount event) {
        cli.getCache().getCurrentPlayerData().setLeadersCount(event.getLeadersCount());
    }

    public void on(Cli cli, UpdateMarket event) {
        cli.getCache().getGameData().setMarket(event.getMarket());
    }

    public void on(Cli cli, UpdatePlayer event) {
        cli.getCache().setPlayerData(event.getPlayer(), new PlayerData(
            event.getBaseProduction(),
            event.getPlayerSetup(),
            event.getStrongbox(),
            event.getWarehouseShelves()));
    }

    public void on(Cli cli, UpdatePlayerStatus event) {
        cli.getCache().getCurrentPlayerData().setActive(event.isActive());
    }

    public void on(Cli cli, UpdateResourceContainer event) {
        cli.getCache().getGameData().setContainer(event.getResContainer());
    }

    public void on(Cli cli, UpdateSetupDone event) {
        // cli.setState(new TurnBeforeActionState());
    }

    public void on(Cli cli, UpdateVaticanSection event) {
        cli.getCache().getGameData().setVaticanSection(event.getVaticanSection());
    }

    public void on(Cli cli, UpdateVictoryPoints event) {
        cli.getCache().getCurrentPlayerData().setVictoryPoints(event.getVictoryPoints());
    }
}
