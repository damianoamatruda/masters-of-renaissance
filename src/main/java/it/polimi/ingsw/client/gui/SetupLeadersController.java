package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.components.*;
import it.polimi.ingsw.common.events.mvevents.UpdateBookedSeats;
import it.polimi.ingsw.common.events.mvevents.UpdateLeadersHand;
import it.polimi.ingsw.common.events.vcevents.ReqChooseLeaders;
import it.polimi.ingsw.common.events.vcevents.ReqJoin;
import it.polimi.ingsw.common.events.vcevents.ReqNewGame;
import it.polimi.ingsw.common.reducedmodel.*;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class SetupLeadersController extends GuiController {
    public HBox leadersContainer;

    private List<Integer> chosen = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        Gui gui = Gui.getInstance();

         if (gui.getViewModel().getPlayerLeaderCards(gui.getViewModel().getLocalPlayerNickname()) == null)
             throw new RuntimeException();

         leadersContainer.setSpacing(10);
         leadersContainer.setAlignment(Pos.CENTER);

        List<LeaderCard> leaderCards = gui.getViewModel().getPlayerLeaderCards(gui.getViewModel().getLocalPlayerNickname()).stream().map(reducedLeader -> {
            LeaderCard leaderCard = new LeaderCard(reducedLeader.getLeaderType());
            leaderCard.setLeaderId(reducedLeader.getId());
            leaderCard.setLeaderType(reducedLeader.getLeaderType());
            leaderCard.setVictoryPoints(reducedLeader.getVictoryPoints()+"");
            leaderCard.setResourceType(reducedLeader.getResourceType().getName());
            leaderCard.setRequirement(reducedLeader.getResourceRequirement());
            leaderCard.setRequirement(reducedLeader.getDevCardRequirement());

            if(reducedLeader.getLeaderType().equalsIgnoreCase("ZeroLeader"))
                leaderCard.setZeroReplacement(reducedLeader.getResourceType());
            else if(reducedLeader.getLeaderType().equalsIgnoreCase("DiscountLeader"))
                leaderCard.setDiscount(reducedLeader.getResourceType(), reducedLeader.getDiscount());
            else if(reducedLeader.getLeaderType().equalsIgnoreCase("ProductionLeader"))
                leaderCard.setProduction(gui.getViewModel().getProduction(reducedLeader.getProduction()).orElseThrow());
            else
                leaderCard.setDepotContent(gui.getViewModel().getContainer(reducedLeader.getContainerId()).orElseThrow(),
                        reducedLeader.getResourceType().getName());

            leaderCard.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                Optional<Integer> id = chosen.stream().filter(l -> leaderCard.getLeaderId() == l).findAny();
                if(id.isEmpty()) chosen.add(leaderCard.getLeaderId());
                else chosen.remove(Integer.valueOf(leaderCard.getLeaderId()));
            });

            return leaderCard;
        }).toList();

        System.out.println(leaderCards.stream().map(LeaderCard::getLeaderId).toList());

        leadersContainer.getChildren().addAll(leaderCards);

    }

    public void handleChoice(){
        Gui gui = Gui.getInstance();
        gui.dispatch(new ReqChooseLeaders(chosen));
    }

    @Override
    public void on(Gui gui, UpdateLeadersHand event) {
        super.on(gui, event);
        try {
            gui.setRoot("playground");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
