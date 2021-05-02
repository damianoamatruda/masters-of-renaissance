package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.messages.Message;

public interface MessageSender {
    void send(Message message);

    void stop();
}
