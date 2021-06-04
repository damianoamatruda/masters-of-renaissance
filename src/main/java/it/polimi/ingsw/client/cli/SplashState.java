package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.MainTitle;

public class SplashState extends CliState {
    @Override
    public void render(Cli cli) {
        new MainTitle().render(cli);
        renderCredits(cli);
        cli.getOut().println();
        renderCastle(cli);
        cli.getOut().println();
        pausePressEnter(cli);
        cli.setState(new MainMenuState());
    }

    private void renderCredits(Cli cli) {
        cli.getOut().print(Cli.center(Cli.convertStreamToString(CliState.class.getResourceAsStream("/assets/cli/credits.txt"))));
    }

    private void renderCastle(Cli cli) {
        cli.getOut().print(Cli.center(Cli.convertStreamToString(CliState.class.getResourceAsStream("/assets/cli/castle.txt"))));
    }

    private void pausePressEnter(Cli cli) {
        cli.getOut().print(Cli.center("[Press ENTER]"));
        cli.pause();
    }
}
