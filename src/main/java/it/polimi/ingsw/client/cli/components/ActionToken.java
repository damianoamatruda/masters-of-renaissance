package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedActionToken;

public class ActionToken extends StringComponent {
    private final ReducedActionToken reducedActionToken;

    public ActionToken(ReducedActionToken reducedActionToken) {
        this.reducedActionToken = reducedActionToken;
    }

    @Override
    public String getString(Cli cli) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("--- ActionToken (ID: \u001B[1m\u001B[37m%d\u001B[0m) ---", reducedActionToken.getId())).append("\n");
        stringBuilder.append(String.format("Kind: %s", reducedActionToken.getKind())).append("\n");
        if (reducedActionToken.getDiscardedDevCardColor() != null)
            stringBuilder.append(String.format("Discarded development card of color %s", new DevCardColor(reducedActionToken.getDiscardedDevCardColor()).getString(cli))).append("\n");

        return Cli.center(stringBuilder.toString());
    }
}
