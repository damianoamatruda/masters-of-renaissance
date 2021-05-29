package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.UpdateBookedSeats;
import it.polimi.ingsw.common.events.mvevents.UpdateCurrentPlayer;
import it.polimi.ingsw.common.events.mvevents.UpdateGame;
import it.polimi.ingsw.common.events.mvevents.UpdateJoinGame;
import it.polimi.ingsw.common.events.mvevents.UpdateLeadersHand;
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
        if (event.canPrepareNewGame().equals(cli.getViewModel().getLocalPlayerNickname()))
            cli.setState(new InputPlayersCountState());
        else
            cli.getOut().printf("%d players waiting for a new game...", event.getBookedSeats());
    }

    @Override
    public void on(Cli cli, UpdateJoinGame event) {
        if (!cli.isOffline())
            cli.getOut().printf("A new player joined the game! Getting to %d...", event.getPlayersCount());
    }
}
