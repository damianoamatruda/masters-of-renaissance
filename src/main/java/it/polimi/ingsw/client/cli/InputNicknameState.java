package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.ReducedObjectPrinter;
import it.polimi.ingsw.common.events.vcevents.ReqJoin;
import it.polimi.ingsw.common.reducedmodel.ReducedGame;

import java.io.PrintStream;
import java.util.Scanner;

public class InputNicknameState extends CliState{
    @Override
    public void render(Cli cli, PrintStream out, Scanner in, ReducedGame cache, ReducedObjectPrinter printer) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String input = cli.prompt(out, in, "Nickname");
        cache.setNickname(input);
        cli.sendToView(new ReqJoin(input));

        //send to view
        //if sth goes wrong, the view will reset this state, thus repeating the prompt.
        // Otherwise, the receival of a positive event will trigger a change of state (by the view??)
    }
}
