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
        cli.getOut().printf("ActionToken ID: %d, kind: %s%n",
                reducedActionToken.getId(),
                reducedActionToken.getKind()
        );
        cli.getOut().println(reducedActionToken.getDiscardedDevCardColor() == null ? "" :
                String.format("Color of discarded development card: %s\n", new Color(reducedActionToken.getDiscardedDevCardColor()).getString(cli)));
    }
}