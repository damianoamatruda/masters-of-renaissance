package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.ErrNewGame;
import it.polimi.ingsw.common.events.vcevents.ReqNewGame;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;

import static it.polimi.ingsw.client.cli.Cli.center;

public class InputPlayersCountState extends CliController {
    @Override
    public void render() {
        if (cli.getUi().isOffline()) {
            // cli.getOut().println(center("Preparing a new game..."));
            cli.getUi().dispatch(new ReqNewGame(1));
            return;
        }

        cli.getOut().println();
        cli.getOut().println(center("~ Play Online ~"));

        cli.getOut().println();
        cli.getOut().println(center("You are the first player in the lobby."));

        cli.getOut().println();

        boolean valid = false;
        while (!valid) {
            valid = true;
            try {
                cli.promptInt("Players count").ifPresentOrElse(count -> {
                    if (count <= 0)
                        throw new NumberFormatException();
                    cli.getUi().dispatch(new ReqNewGame(count));
                }, () -> cli.getUi().dispatch(new ReqQuit()));
            } catch (NumberFormatException e) {
                cli.getOut().println("Please input an integer greater than 0.");
                valid = false;
            }
        }
    }

    @Override
    public void on(ErrNewGame event) {
        cli.reloadController(event.isInvalidPlayersCount() ?
                "Invalid players count." :
                // should technically never happen
                "You are not supposed to choose the players count for the game.");
    }

    @Override
    public void on(UpdateBookedSeats event) {
        cli.getOut().printf("%d players waiting for a new game...", event.getBookedSeats());
    }

    @Override
    public void on(UpdateJoinGame event) {
        if (!cli.getUi().isOffline())
            cli.getOut().printf("A new player joined the game! Getting to %d...%n%n", event.getPlayersCount());
    }

    @Override
    public void on(UpdateGame event) {
        super.on(event);

        setNextSetupState();
    }

    @Override
    public void on(UpdatePlayer event) {
        super.on(event);

        setNextSetupState();
    }

    @Override
    public void on(UpdateCurrentPlayer event) {
        super.on(event);

        setNextSetupState();
    }

    @Override
    public void on(UpdateLeadersHand event) {
        super.on(event);

        new Thread(() -> {
            cli.promptPause();
            setNextSetupState();
        }).start();
    }
}
