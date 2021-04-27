package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.Controller;

public class GameProtocol {
    private final Controller controller;

    public GameProtocol(Controller controller) {
        this.controller = controller;
    }

    public String processInput(String input) {
        if (input == null) return "Welcome.";
        System.out.println("Received: \"" + input + "\"");
        if (input.equals("quit")) return "Bye.";

        /* Interprets command string and calls an action from the model */

        return input + "!";
    }
}
