package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.*;
import it.polimi.ingsw.server.Controller;

public class ControllerObservable {
    /** The controller observing the view. */
    private final Controller controller;

    public ControllerObservable(Controller c) { this.controller = c; }

    public void notify(ReqQuit event) {
        controller.update(this, event);
    }

    public void notify(ReqNickname event) {
        controller.update(this, event);
    }

    public void notify(ReqActivateLeader event) {
        controller.update(this, event);
    }

    public void notify(ReqActivateProduction event) {
        controller.update(this, event);
    }

    public void notify(ReqBuyDevCard event) {
        controller.update(this, event);
    }

    public void notify(ReqDiscardLeader event) {
        controller.update(this, event);
    }

    public void notify(ReqChooseLeaders event) {
        controller.update(this, event);
    }

    public void notify(ReqPlayersCount event) {
        controller.update(this, event);
    }

    public void notify(ReqChooseResources event) {
        controller.update(this, event);
    }

    public void notify(ReqSwapShelves event) {
        controller.update(this, event);
    }

    public void notify(ReqTakeFromMarket event) {
        controller.update(this, event);
    }

    public void notify(ReqTurnEnd event) {
        controller.update(this, event);
    }

}
