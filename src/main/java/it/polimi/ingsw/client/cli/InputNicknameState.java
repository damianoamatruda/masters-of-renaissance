package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.UpdateBookedSeats;
import it.polimi.ingsw.common.events.mvevents.UpdateGame;
import it.polimi.ingsw.common.events.mvevents.UpdateJoinGame;
import it.polimi.ingsw.common.events.mvevents.errors.ErrNickname;
import it.polimi.ingsw.common.events.vcevents.ReqJoin;
import it.polimi.ingsw.common.events.vcevents.ReqNewGame;

public class InputNicknameState extends CliState{
    @Override
    public void render(Cli cli) {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
        if (cli.isOffline())
            cli.dispatch(new ReqNewGame(1));
        else {
            if (event.canPrepareNewGame() == cli.getViewModel().getLocalPlayerNickname())
                cli.setState(new InputPlayersCountState());
            else
                cli.getOut().printf("%d players waiting for a new game...", event.getBookedSeats());
        }
    }

    @Override
    public void on(Cli cli, UpdateJoinGame event) {
        cli.getOut().printf("A new player joined the game! Getting to %d...", event.getPlayersCount());
    }

    @Override
    public void on(Cli cli, UpdateGame event) {
        super.on(cli, event); // need to update the cache
        if (event.isResumed())
            cli.setState(new WaitingAfterTurnState());
        else
            cli.setState(
                new SetupLeadersState(cli.getViewModel()
                    .getLocalPlayerData()
                    .getSetup()
                    .getChosenLeadersCount()));
    }

    // decided to implement it at the CliState level, if issues bring it down a level
    // @Override
    // public void on(Cli cli, UpdateCurrentPlayer event) {
    //     // having this overriding may prove necessary:
    //     // if the setState isn't fast enough, the next event after UpdateGame is CurrentPlayer and
    //     // this means it might not get handled in WaitingAfterTurnState
    //     super.on(cli, event);

    //     if (event.getPlayer().equals(cli.getCache().getUiData().getLocalPlayerNickname()))
    //         cli.setState(new TurnBeforeActionState());
    //     else
    //         cli.setState(new WaitingAfterTurnState());
    // }
}
