package it.polimi.ingsw.client.gui;

import java.net.URL;
import java.util.*;

import it.polimi.ingsw.client.gui.components.DevSlot;
import it.polimi.ingsw.client.gui.components.DevelopmentCard;
import it.polimi.ingsw.client.gui.components.Playerboard;
import it.polimi.ingsw.client.gui.components.Production;
import it.polimi.ingsw.client.gui.components.Strongbox;
import it.polimi.ingsw.client.gui.components.Warehouse;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.client.gui.components.*;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.UpdateCurrentPlayer;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public abstract class PlaygroundController extends GuiController {
    @FXML
    protected AnchorPane canvas;
    @FXML
    protected Playerboard pboard;

    private VBox leadersBox = new VBox();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        Gui gui = Gui.getInstance();

        ViewModel vm = gui.getViewModel();

        int baseProdId = gui.getViewModel().getCurrentPlayerData().getBaseProduction();

        Production prod = new Production();
        prod.setStyle("-fx-background-image: url('/assets/gui/playerboard/baseproduction.png');" +
                "-fx-background-position: center center;" +
                "-fx-alignment: center;" +
                "-fx-background-repeat: stretch;" +
                "-fx-opacity: 1;" +
                "-fx-background-size: 100 100;");
        prod.setProduction(gui.getViewModel().getProduction(baseProdId).orElseThrow());


        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseShelves(vm.getPlayerShelves(vm.getCurrentPlayer()));


        Strongbox s = new Strongbox();
        int sbId = gui.getViewModel().getCurrentPlayerData().getStrongbox();

        s.setContent(gui.getViewModel().getContainer(sbId).orElseThrow());

        List<DevSlot> slots = new ArrayList<>();

        List<List<Integer>> modelSlots = vm.getCurrentPlayerData().getDevSlots();
        for (List<Integer> modelSlot : modelSlots) {
            DevSlot slot = new DevSlot();

            List<DevelopmentCard> cards = modelSlot.stream()
                    .map(i -> new DevelopmentCard(vm.getDevelopmentCard(i).orElseThrow()))
                    .toList();
            slot.setDevCards(cards);

            slots.add(slot);
        }
        for(int i = 0; i < vm.getSlotsCount() - modelSlots.size(); i++){
            slots.add(new DevSlot());
        }

        FaithTrack f = new FaithTrack(vm.getFaithTrack());

        pboard = new Playerboard(warehouse, s, prod, slots, f);

        canvas.getChildren().add(pboard);

        AnchorPane.setBottomAnchor(pboard, 0d);
        AnchorPane.setTopAnchor(pboard, 0d);
        AnchorPane.setLeftAnchor(pboard, 0d);
        AnchorPane.setRightAnchor(pboard, 0d);


        canvas.setBorder(new Border(new BorderStroke(Color.PINK,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        pboard.setBorder(new Border(new BorderStroke(Color.BLUE,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

    }

    protected void setLeadersBox() {
        ViewModel vm = Gui.getInstance().getViewModel();
        List<LeaderCard> leaders = vm.getCurrentPlayerData().getLeadersHand().stream()
            .map(id -> vm.getLeaderCard(id).orElseThrow())
            .map(reducedLeader -> {
                LeaderCard leaderCard = new LeaderCard(reducedLeader.getLeaderType());
                leaderCard.setLeaderId(reducedLeader.getId());
                leaderCard.setLeaderType(reducedLeader.getLeaderType());
                leaderCard.setVictoryPoints(reducedLeader.getVictoryPoints() + "");
                leaderCard.setResourceType(reducedLeader.getResourceType().getName());
                leaderCard.setRequirement(reducedLeader.getResourceRequirement());
                leaderCard.setRequirement(reducedLeader.getDevCardRequirement());

                if(reducedLeader.getLeaderType().equalsIgnoreCase("ZeroLeader"))
                    leaderCard.setZeroReplacement(reducedLeader.getResourceType());
                else if(reducedLeader.getLeaderType().equalsIgnoreCase("DiscountLeader"))
                    leaderCard.setDiscount(reducedLeader.getResourceType(), reducedLeader.getDiscount());
                else if(reducedLeader.getLeaderType().equalsIgnoreCase("ProductionLeader"))
                    leaderCard.setProduction(vm.getProduction(reducedLeader.getProduction()).orElseThrow());
                else
                    leaderCard.setDepotContent(vm.getContainer(reducedLeader.getContainerId()).orElseThrow(),
                            reducedLeader.getResourceType().getName());

                return leaderCard;
            }).toList();

        leadersBox.setAlignment(Pos.CENTER);
        leadersBox.setPrefWidth(166);
        leadersBox.setSpacing(20);
        leadersBox.getChildren().addAll(leaders);
        canvas.getChildren().add(leadersBox);
        AnchorPane.setRightAnchor(leadersBox, 10.0);
        AnchorPane.setBottomAnchor(leadersBox, 50.0);
    }

//    @Override
//    public void on(Gui gui, UpdateFaithPoints event) {
//        super.on(gui, event);
//        pboard.updateFaithPoints(event);
//    }


    @Override
    public void on(Gui gui, UpdateCurrentPlayer event) {
        super.on(gui, event);
        if(event.getPlayer().equals(gui.getViewModel().getLocalPlayerNickname()))
            gui.setRoot(getClass().getResource("/assets/gui/playgroundbeforeaction.fxml"));
        else
            gui.setRoot(getClass().getResource("/assets/gui/waitingforturn.fxml"));

    }
}
