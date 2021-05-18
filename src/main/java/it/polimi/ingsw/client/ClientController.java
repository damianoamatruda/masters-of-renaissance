package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.reducedmodel.ReducedGame;

public class ClientController {
    private ReducedGame model;

    public ClientController(ReducedGame model) {
        this.model = model;
    }

    public void registerToView(View view) {
        view.addEventListener(UpdateVictoryPoints.class, event -> on(view, event));
        view.addEventListener(UpdateVaticanSection.class, event -> on(view, event));
        view.addEventListener(UpdateMarket.class, event -> on(view, event));
        view.addEventListener(UpdateResourceContainer.class, event -> on(view, event));
        view.addEventListener(UpdatePlayer.class, event -> on(view, event));
        view.addEventListener(UpdateLeadersHand.class, event -> on(view, event));
        view.addEventListener(UpdateLeader.class, event -> on(view, event));
        view.addEventListener(UpdateLeadersHandCount.class, event -> on(view, event));
        view.addEventListener(UpdateDevCardSlot.class, event -> on(view, event));
        view.addEventListener(UpdateFaithPoints.class, event -> on(view, event));
        view.addEventListener(UpdateActionToken.class, event -> on(view, event));
        view.addEventListener(UpdateSetupDone.class, event -> on(view, event));
        view.addEventListener(UpdateSetupDone.class, event -> on(view, event));
        view.addEventListener(UpdateSetupDone.class, event -> on(view, event));
        view.addEventListener(UpdateSetupDone.class, event -> on(view, event));

    }

    public void unregisterToView(View view) {
        view.removeEventListener(UpdateVictoryPoints.class, event -> on(view, event));
        view.removeEventListener(UpdateVaticanSection.class, event -> on(view, event));
        view.removeEventListener(UpdateMarket.class, event -> on(view, event));
        view.removeEventListener(UpdateResourceContainer.class, event -> on(view, event));
        view.removeEventListener(UpdatePlayer.class, event -> on(view, event));
        view.removeEventListener(UpdateLeadersHand.class, event -> on(view, event));
        view.removeEventListener(UpdateLeader.class, event -> on(view, event));
        view.removeEventListener(UpdateLeadersHandCount.class, event -> on(view, event));
        view.removeEventListener(UpdateDevCardSlot.class, event -> on(view, event));
        view.removeEventListener(UpdateFaithPoints.class, event -> on(view, event));
        view.removeEventListener(UpdateActionToken.class, event -> on(view, event));
        view.removeEventListener(UpdateSetupDone.class, event -> on(view, event));
    }

    private void on(View view, UpdateVictoryPoints event) {
        model.setVictoryPoints(event.getVictoryPoints());
    }

    private void on(View view, UpdateVaticanSection event) {
    }

    private void on(View view, UpdateMarket event) {
        model.setMarket(event.getMarket());
    }

    private void on(View view, UpdateResourceContainer event) {
        model.setContainer(event.getResContainer());
    }

    private void on(View view, UpdatePlayer event) {
    }

    private void on(View view, UpdateLeadersHand event) {
    }

    private void on(View view, UpdateLeader event) {
    }

    private void on(View view, UpdateLeadersHandCount event) {
    }

    private void on(View view, UpdateDevCardSlot event) {
    }

    private void on(View view, UpdateFaithPoints event) {
    }

    private void on(View view, UpdateActionToken event) {
    }

    private void on(View view, UpdateSetupDone event) {
    }

}
