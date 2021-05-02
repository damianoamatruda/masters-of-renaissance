package it.polimi.ingsw.server.view;

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

    /**
     * Class constructor.
     *
     * @param controller the controller observing the view
     */
    public View(Controller controller) {
        this.controller = controller;
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

    // public void update(Message message) {}
}
