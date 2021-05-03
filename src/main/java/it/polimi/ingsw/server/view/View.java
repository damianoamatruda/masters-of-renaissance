package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.MessageSender;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.messages.*;

/**
 * This class represents a virtual view.
 */
public class View {
    // /** The nickname of the user owning the view. */
    // private final String nickname;

    /** The controller observing the view. */
    private final Controller controller;

    /** The message sender of the view. */
    private MessageSender messageSender;

    /**
     * Class constructor.
     *
     * @param controller the controller observing the view
     */
    public View(Controller controller) {
        this.controller = controller;
        this.messageSender = null;
    }

    /**
     * Sets the message sender.
     *
     * @param messageSender the message sender
     */
    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void notify(ReqQuit message) {
        controller.update(this, message);
    }

    public void notify(ReqNickname message) {
        controller.update(this, message);
    }

    public void notify(ReqActivateLeader message) {
        controller.update(this, message);
    }

    public void notify(ReqActivateProduction message) {
        controller.update(this, message);
    }

    public void notify(ReqBuyDevCard message) {
        controller.update(this, message);
    }

    public void notify(ReqDiscardLeader message) {
        controller.update(this, message);
    }

    public void notify(ReqGetMarket message) {
        controller.update(this, message);
    }

    public void notify(ReqChooseLeaders message) {
        controller.update(this, message);
    }

    public void notify(ReqPlayersCount message) {
        controller.update(this, message);
    }

    public void notify(ReqChooseResources message) {
        controller.update(this, message);
    }

    public void notify(ReqSwapShelves message) {
        controller.update(this, message);
    }

    public void notify(ReqTakeFromMarket message) {
        controller.update(this, message);
    }

    public void notify(ReqTurnEnd message) {
        controller.update(this, message);
    }

    public void update(ResWelcome message) {
        if (messageSender != null)
            messageSender.send(message);
    }

    public void update(ResGoodbye message) {
        if (messageSender != null) {
            messageSender.send(message);
            messageSender.stop();
            messageSender = null;
        }
    }

    public void update(ErrCommunication message) {
        if (messageSender != null)
            messageSender.send(message);
    }

    public void update(ErrController message) {
        if (messageSender != null)
            messageSender.send(message);
    }

    public void update(ResNickname message) {
        if (messageSender != null)
            messageSender.send(message);
    }

    public void update(ErrNickname message) {
        if (messageSender != null)
            messageSender.send(message);
    }

    public void update(ResGameStarted message) {
        if (messageSender != null)
            messageSender.send(message);
    }

    public void update(ErrAction message) {
        if (messageSender != null)
            messageSender.send(message);
    }
}
