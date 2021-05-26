package it.polimi.ingsw.client.cli;

public class PlayOfflineState extends CliState {
    @Override
    public void render(Cli cli) {
        cli.startOfflineClient();
        cli.setState(new InputNicknameState());
    }
}
