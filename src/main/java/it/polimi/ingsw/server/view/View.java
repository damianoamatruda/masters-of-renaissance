package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.MessageSender;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.messages.*;
import it.polimi.ingsw.server.model.DevelopmentCard;
import it.polimi.ingsw.server.model.Production;
import it.polimi.ingsw.server.model.leadercards.LeaderCard;
import it.polimi.ingsw.server.model.resourcecontainers.ResourceContainer;

import java.util.List;

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

    public void updateWelcome() {
        if (messageSender != null)
            messageSender.send(new ResWelcome());
    }

    public void updateEmptyInput() {
        if (messageSender != null)
            messageSender.send(new ErrCommunication("Empty input."));
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
            messageSender.send(new ErrCommunication("Invalid syntax."));
    }

    public void updateUnknownParserError() {
        if (messageSender != null)
            messageSender.send(new ErrCommunication("Unknown parser error."));
    }

    public void updateFieldTypeNotFound() {
        if (messageSender != null)
            messageSender.send(new ErrCommunication("Field \"type\" not found."));
    }

    public void updateMessageTypeDoesNotExist(String type) {
        if (messageSender != null)
            messageSender.send(new ErrCommunication("Message type \"" + type + "\" does not exist."));
    }

    public void updateInvalidAttributeTypes() {
        if (messageSender != null)
            messageSender.send(new ErrCommunication("Invalid attribute types."));
    }

    public void updateControllerError(Exception e) {
        if (messageSender != null)
            messageSender.send(new ErrController(e));
    }

    public void updateNickname(boolean isFirst) {
        if (messageSender != null)
            messageSender.send(new ResNickname(isFirst));
    }

    public void updateNicknameError(String message) {
        if (messageSender != null)
            messageSender.send(new ErrNickname(message));
    }

    public void updateGameStarted(List<LeaderCard> leaderCards, List<DevelopmentCard> developmentCards,
                                  List<ResourceContainer> resContainers, List<Production> productions) {
        if (messageSender != null)
            messageSender.send(new ResGameStarted(leaderCards, developmentCards, resContainers, productions));
    }

    public void updateActionError(String message) {
        if (messageSender != null)
            messageSender.send(new ErrAction(message));
    }
}
