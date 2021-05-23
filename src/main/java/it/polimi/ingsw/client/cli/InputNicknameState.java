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

        String nickname;

        do {
            nickname = cli.prompt(out, in, "Nickname");
        } while (nickname.isBlank());

        cache.setNickname(nickname);
        cli.dispatch(new ReqJoin(nickname));
    }
}
