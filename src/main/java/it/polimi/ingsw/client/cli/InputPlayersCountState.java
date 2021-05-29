package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.UpdateBookedSeats;
import it.polimi.ingsw.common.events.mvevents.UpdateCurrentPlayer;
import it.polimi.ingsw.common.events.mvevents.UpdateGame;
import it.polimi.ingsw.common.events.mvevents.UpdateJoinGame;
import it.polimi.ingsw.common.events.mvevents.UpdateLeadersHand;
import it.polimi.ingsw.common.events.mvevents.errors.ErrNewGame;
import it.polimi.ingsw.common.events.vcevents.ReqNewGame;

public class InputPlayersCountState extends CliState {
    @Override
    public void render(Cli cli) {
        if (cli.isOffline()) {
            cli.getOut().println("Preparing a new game...");
            cli.dispatch(new ReqNewGame(1));
            return;
        }

        int count = 0;
        boolean isNumber = false;
        while (!isNumber) {
            try {
                String input = cli.prompt("You are the first player of the match. Please choose the players count");
                count = Integer.parseInt(input);
                
                if (count <= 0)
                    throw new NumberFormatException();
                
                isNumber = true;
            } catch (NumberFormatException e) {
                cli.getOut().println("Please input an integer greater than 0.");
            }
        }

        cli.dispatch(new ReqNewGame(count));
    }

    @Override
    public void on(Cli cli, ErrNewGame event) {
        cli.repeatState(event.isInvalidPlayersCount() ?
                "Invalid players count." :
                // should technically never happen
                "You are not supposed to choose the players count for the game.");
    }

    @Override
    public void on(Cli cli, UpdateBookedSeats event) {
        // if (cli.isSingleplayer())
        //     cli.dispatch(new ReqNewGame(1));
        // else {
            // if (event.canPrepareNewGame() == cli.getCache().getUiData().getLocalPlayerNickname())
            //     cli.setState(new InputPlayersCountState());
            // else
                cli.getOut().printf("%d players waiting for a new game...", event.getBookedSeats());
        // }
    }

    @Override
    public void on(Cli cli, UpdateJoinGame event) {
        cli.getOut().printf("A new player joined the game! Getting to %d...", event.getPlayersCount());
    }

    @Override
    public void on(Cli cli, UpdateCurrentPlayer event) {
        // having this overriding may prove necessary:
        // if the setState isn't fast enough, the next event after UpdateGame is CurrentPlayer and
        // this means it might not get handled in WaitingAfterTurnState

        if (cli.getViewModel().isResumedGame()) {
            if (event.getPlayer().equals(cli.getViewModel().getLocalPlayerNickname()))
                cli.setState(new TurnBeforeActionState());
            else
                cli.setState(new WaitingAfterTurnState());
        }
    }

    @Override
    public void on(Cli cli, UpdateLeadersHand event) {
        super.on(cli, event);

        if (cli.getViewModel().isResumedGame())
            throw new RuntimeException("UpdateLeadersHand after resumed game.");
        
        cli.setState(
            new SetupLeadersState(cli.getViewModel()
                .getLocalPlayerData()
                .getSetup()
                .getChosenLeadersCount()));
    }
}
