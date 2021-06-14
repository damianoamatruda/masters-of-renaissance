package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedActionToken;

import static it.polimi.ingsw.client.cli.Cli.center;
import static it.polimi.ingsw.client.cli.Cli.centerAll;

public class ActionToken extends StringComponent {
    private final ReducedActionToken reducedActionToken;

    public ActionToken(ReducedActionToken reducedActionToken) {
        this.reducedActionToken = reducedActionToken;
    }

    @Override
    public String getString(Cli cli) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("--- ActionToken (ID: \u001B[1m\u001B[37m%d\u001B[0m) ---", reducedActionToken.getId())).append("\n");
        
        if (reducedActionToken.getKind().contains("Black"))
            if (reducedActionToken.getKind().contains("Shuffle"))
                stringBuilder.append("Moved Lorenzo il Magnifico's cross one tile and shuffled the tokens.");
            else
                stringBuilder.append("Moved Lorenzo il Magnifico's cross two spaces.");
        
        reducedActionToken.getDiscardedDevCardColor().ifPresent(color ->
                stringBuilder.append(
                        String.format("Discarded development cards of color %s.",
                                new DevCardColor(color).getString(cli))).append("\n"));

        return centerAll(stringBuilder.toString());
    }
}
