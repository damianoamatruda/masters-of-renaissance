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
        controller.quit(this);
    }

    public void notify(ReqNickname message) {
        controller.registerNickname(this, message.getNickname());
    }

    public void notify(ReqActivateLeader message) {
        controller.activateLeader(this, message.getLeader());
    }

    public void notify(ReqActivateProduction message) {
        controller.activateProduction(this);
    }

    public void notify(ReqBuyDevCard message) {
        controller.buyDevCard(this);
    }

    public void notify(ReqDiscardLeader message) {
        controller.discardLeader(this, message.getLeader());
    }

    public void notify(ReqGetMarket message) {
        controller.getMarket(this);
    }

    public void notify(ReqChooseLeaders message) {
        controller.chooseLeaders(this);
    }

    public void notify(ReqPlayersCount message) {
        controller.setPlayersCount(this);
    }

    public void notify(ReqResourcesChoice message) {
        controller.chooseResources(this);
    }

    public void notify(ReqSwapShelves message) {
        controller.swapShelves(this);
    }

    public void notify(ReqTakeFromMarket message) {
        controller.takeFromMarket(this);
    }

    public void notify(ReqTurnEnd message) {
        controller.endTurn(this);
    }

    public void updateWelcome() {
        if (messageSender != null)
            messageSender.send(new ResWelcome());
    }

    public void updateEmptyInput() {
        if (messageSender != null)
            messageSender.send(new ResCommunicationError("Empty input."));
    }

    public void updateGoodbye() {
        if (messageSender != null) {
            messageSender.send(new ResGoodbye());
            messageSender.stop();
            messageSender = null;
        }
    }

    public void updateInvalidSyntax() {
        if (messageSender != null)
            messageSender.send(new ResCommunicationError("Invalid syntax."));
    }

    public void updateUnknownParserError() {
        if (messageSender != null)
            messageSender.send(new ResCommunicationError("Unknown parser error."));
    }

    public void updateFieldTypeNotFound() {
        if (messageSender != null)
            messageSender.send(new ResCommunicationError("Field \"type\" not found."));
    }

    public void updateMessageTypeDoesNotExist(String type) {
        if (messageSender != null)
            messageSender.send(new ResCommunicationError("Message type \"" + type + "\" does not exist."));
    }

    public void updateInvalidAttributeTypes() {
        if (messageSender != null)
            messageSender.send(new ResCommunicationError("Invalid attribute types."));
    }

    public void updateControllerError(String message) {
        if (messageSender != null)
            messageSender.send(new ResCommunicationError("Controller error: " + message));
    }
}
