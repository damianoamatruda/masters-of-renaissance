package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.client.cli.Renderable;
import it.polimi.ingsw.common.reducedmodel.ReducedActionToken;

public class ActionToken implements Renderable {
    private final ReducedActionToken reducedActionToken;

    public ActionToken(ReducedActionToken reducedActionToken) {
        this.reducedActionToken = reducedActionToken;
    }

    @Override
    public void render(Cli cli) {
        cli.getOut().printf("--- ActionToken (ID: \u001B[1m\u001B[37m%d\u001B[0m) ---%n", reducedActionToken.getId());
        cli.getOut().printf("Kind: %s%n", reducedActionToken.getKind());
        if (reducedActionToken.getDiscardedDevCardColor() != null)
            cli.getOut().printf("Discarded development card of color %s%n", new DevCardColor(reducedActionToken.getDiscardedDevCardColor()).getString(cli));
    }
}
