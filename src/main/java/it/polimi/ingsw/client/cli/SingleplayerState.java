package it.polimi.ingsw.client.cli;

public class SingleplayerState extends CliState {
    @Override
    public void render(Cli cli) {
        cli.startLocalClient();
        cli.setState(new InputNicknameState());
    }
}
