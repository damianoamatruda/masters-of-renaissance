package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.MainTitle;

import static it.polimi.ingsw.client.cli.Cli.center;
import static it.polimi.ingsw.client.cli.Cli.convertStreamToString;

public class SplashState extends CliController {
    @Override
    public void render() {
        new MainTitle().render();
        renderCredits(cli);
        cli.getOut().println();
        renderCastle(cli);
        cli.getOut().println();
        pausePressEnter(cli);
        cli.setState(new MainMenuState());
    }

    private void renderCredits(Cli cli) {
        cli.getOut().println(center(convertStreamToString(CliController.class.getResourceAsStream("/assets/cli/credits.txt"))));
    }

    private void renderCastle(Cli cli) {
        cli.getOut().println(center(convertStreamToString(CliController.class.getResourceAsStream("/assets/cli/castle.txt"))));
    }

    private void pausePressEnter(Cli cli) {
        cli.getOut().println(center("[Press ENTER]"));
        cli.pause();
    }
}
