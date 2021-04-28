package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.controller.Controller;

import java.net.Socket;
import java.util.List;

public class ReqSwapShelves implements Message {
    private List<Integer> shelves;

    @Override
    public void handle(Controller controller, Socket client) {

    }
}
