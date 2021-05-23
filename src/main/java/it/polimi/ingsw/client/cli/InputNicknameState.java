package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.vcevents.ReqJoin;

public class InputNicknameState extends CliState{
    @Override
    public void render(Cli cli) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String nickname;

        do {
            nickname = cli.prompt("Nickname");
        } while (nickname.isBlank());

        cli.getCache().setNickname(nickname);
        cli.dispatch(new ReqJoin(nickname));
    }
}
