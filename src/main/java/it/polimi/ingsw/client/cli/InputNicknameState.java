package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.UpdateBookedSeats;
import it.polimi.ingsw.common.events.mvevents.UpdateCurrentPlayer;
import it.polimi.ingsw.common.events.mvevents.UpdateJoinGame;
import it.polimi.ingsw.common.events.mvevents.UpdateLeadersHand;
import it.polimi.ingsw.common.events.mvevents.errors.ErrNickname;
import it.polimi.ingsw.common.events.vcevents.ReqJoin;

public class InputNicknameState extends CliState{
    @Override
    public void render(Cli cli) {
        String nickname;

        do {
            nickname = cli.prompt("Nickname");
        } while (nickname.isBlank());

        cli.getViewModel().setLocalPlayerNickname(nickname);
        cli.dispatch(new ReqJoin(nickname));
    }

    @Override
    public void on(Cli cli, ErrNickname event) {
        // don't call super.on
        cli.repeatState("Nickname is invalid. Reason: " + event.getReason().toString().toLowerCase());
    }

    @Override
    public void on(Cli cli, UpdateBookedSeats event) {
        if (event.canPrepareNewGame().equals(cli.getViewModel().getLocalPlayerNickname()))
            cli.setNextState(new InputPlayersCountState());
        else if (!cli.getViewModel().getLocalPlayerNickname().equals(""))
            cli.getOut().printf("%d players waiting for a new game...", event.getBookedSeats());
    }

    @Override
    public void on(Cli cli, UpdateJoinGame event) {
        if (!cli.isOffline())
            cli.getOut().printf("A new player joined the game! Getting to %d...%n%n", event.getPlayersCount());
    }

    @Override
    public void on(Cli cli, UpdateCurrentPlayer event) {
        // having this overriding may prove necessary:
        // if the setState isn't fast enough, the next event after UpdateGame is CurrentPlayer and
        // this means it might not get handled in WaitingAfterTurnState
        super.on(cli, event);
        
        if (cli.getViewModel().isResumedGame()) {
            if (event.getPlayer().equals(cli.getViewModel().getLocalPlayerNickname()))
                cli.setNextState(new TurnBeforeActionState());
            else
                cli.setNextState(new WaitingAfterTurnState());
        }
    }
    
    @Override
    public void on(Cli cli, UpdateLeadersHand event) {
        super.on(cli, event);

        if (cli.getViewModel().isResumedGame())
            throw new RuntimeException("UpdateLeadersHand after resumed game.");

        cli.getOut().println();
        cli.promptPause();
        cli.setNextState(
                new SetupLeadersState(cli.getViewModel()
                        .getLocalPlayerData().orElseThrow()
                        .getSetup().orElseThrow()
                        .getChosenLeadersCount()));
    }
}
