package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.UpdateBookedSeats;
import it.polimi.ingsw.common.events.vcevents.ReqNewGame;

public class InputPlayersCountState extends CliState {
    @Override
    public void render(Cli cli) {
        int count = 0;
        boolean isNumber = false;
        while (!isNumber) {
            try {
                String input = cli.prompt("You are the first player. Choose players count");
                count = Integer.parseInt(input);
                isNumber = true;
            } catch (NumberFormatException e) {
                cli.getOut().println("Please input an integer.");
            }
        }

        cli.dispatch(new ReqNewGame(count));
        //send
        //if goes wrong, this state will be repeated
    }

    @Override
    public void on(Cli cli, UpdateBookedSeats event) {
    }
}
