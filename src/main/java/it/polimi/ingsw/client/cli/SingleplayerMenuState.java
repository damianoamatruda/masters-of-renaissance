package it.polimi.ingsw.client.cli;

public class SingleplayerMenuState extends CliState {
    @Override
    public void render(Cli cli) {
        cli.clear();
        renderMainTitle(cli);
        for (int i = 0; i < 2; i++)
            cli.getOut().println();

        cli.startLocalClient();
        cli.setState(new InputNicknameState());
    }
}
