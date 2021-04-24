package it.polimi.ingsw.server;

import it.polimi.ingsw.server.model.gamecontext.GameContext;

public class GameProtocol {
    private GameContext context;

    public String processInput(String input) {
        if(input == null) return "Welcome.";
        System.out.println("Received: \""+input+"\"");
        if(input.equals("quit")) return "Bye.";

        //Interprets command string and calls an action from GameContext

        return input + "!";
    }
}
