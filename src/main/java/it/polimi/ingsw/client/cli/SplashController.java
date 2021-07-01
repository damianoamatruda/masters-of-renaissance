package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.MainTitle;

import static it.polimi.ingsw.client.cli.Cli.center;
import static it.polimi.ingsw.client.cli.Cli.convertStreamToString;

public class SplashController extends CliController {
    @Override
    public void render() {
        new MainTitle().render();
        renderCredits();
        cli.getOut().println();
        renderCastle();
        cli.getOut().println();
        pausePressEnter();
        cli.setController(new MainMenuController(), false);
    }

    private void renderCredits() {
        cli.getOut().println(center(convertStreamToString(getClass().getResourceAsStream("/assets/cli/credits.txt"))));
    }

    private void renderCastle() {
        cli.getOut().println(center(convertStreamToString(getClass().getResourceAsStream("/assets/cli/castle.txt"))));
    }

    private void pausePressEnter() {
        cli.getOut().println(center("[Press ENTER]"));
        cli.pause();
    }
}
